package com.example.trustie.repository.audiotranscriptrepo

import android.Manifest
import android.content.Context
import android.hardware.SensorPrivacyManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.trustie.data.GlobalStateManager
import com.example.trustie.data.api.ApiManager
import com.example.trustie.data.local.wave2vec.OnnxWav2Vec2Manager
import com.example.trustie.data.local.wave2vec.TFLiteModelManager
import com.example.trustie.data.local.wave2vec.TranscriptionResult
import com.example.trustie.data.model.response.ScamAnalysisResponse
import com.example.trustie.ui.screen.scamresult.ScamResultData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioTranscriptRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val modelManager: OnnxWav2Vec2Manager,
    private val globalStateManager: GlobalStateManager
) : AudioTranscriptRepository {

    private val _stableTranscript = MutableLiveData<String>()
    override val stableTranscript: LiveData<String> get() = _stableTranscript

    private val _pendingChunk = MutableLiveData<String>()
    override val pendingChunk: LiveData<String> get() = _pendingChunk

    private val _scamDetected = MutableLiveData<Boolean>()
    override val scamDetected: LiveData<Boolean> get() = _scamDetected

    private var audioRecord: AudioRecord? = null
    private var isListening = false
    private var listeningJob: Job? = null

    // Audio recording variables
    private var recordingFile: File? = null
    private var audioFileOutput: FileOutputStream? = null
    private var recordingStartTime: Long = 0
    private var maxRecordingDurationMs = 60_000L // 1 minute

    // FIXED: Use thread-safe collection and mutex for synchronization
    private val audioBuffer = CopyOnWriteArrayList<ShortArray>()
    private val audioBufferMutex = Mutex()

    // Vietnamese scam keywords
    private val scamKeywords: List<String> by lazy {
        loadScamKeywords()
    }

    // Flags to prevent repeated calls
    private var scamFlagTriggered = false
    private var totalRecordingTimeMs: Long = 0
    private val maxBufferSizeBeforeCompression = 50 // chunks before we need to compress
    private var compressionNeeded = false
    private val maxTotalRecordingTimeMs = 300_000L // 5 minutes total

    // ADD: Audio processing helpers
    private fun amplifyAudio(buffer: ShortArray, gain: Float = 2.5f): ShortArray {
        val amplified = ShortArray(buffer.size)
        for (i in buffer.indices) {
            val amplifiedValue = (buffer[i] * gain).toInt()
            amplified[i] = amplifiedValue.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return amplified
    }

    private fun calculateRMS(buffer: ShortArray): Double {
        var sum = 0.0
        for (sample in buffer) {
            sum += sample * sample
        }
        return kotlin.math.sqrt(sum / buffer.size)
    }

    private fun isSpeech(buffer: ShortArray, threshold: Double = 300.0): Boolean {
        val rms = calculateRMS(buffer)
        return rms > threshold
    }

    private fun loadScamKeywords(): List<String> {
        return try {
            context.assets.open("scam_keywords.txt").use { inputStream ->
                inputStream.bufferedReader().useLines { lines ->
                    lines
                        .map { it.trim() }
                        .filter { it.isNotEmpty() && !it.startsWith("#") }
                        .toList()
                }
            }
        } catch (e: Exception) {
            Log.e("AudioTranscriptRepo", "Error loading scam keywords", e)
            listOf(
                "tài khoản ngân hàng",
                "mật khẩu",
                "otp",
                "chuyển khoản",
                "thẻ tín dụng",
                "lừa đảo",
                "nạp tiền",
                "vay tiền",
                "thanh toán",
                "đơn hàng",
                "trúng thưởng"
            )
        }
    }

    private fun createTempAudioFile(): File {
        val tempDir = File(context.cacheDir, "audio_recordings")
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        return File(tempDir, "temp_recording_${System.currentTimeMillis()}.wav")
    }

    private fun intToByteArray(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte(),
            ((value shr 16) and 0xFF).toByte(),
            ((value shr 24) and 0xFF).toByte()
        )
    }

    private fun shortToByteArray(value: Short): ByteArray {
        return byteArrayOf(
            (value.toInt() and 0xFF).toByte(),
            ((value.toInt() shr 8) and 0xFF).toByte()
        )
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun startListening() {
        if (isListening) return

        isListening = true
        scamFlagTriggered = false
        recordingStartTime = System.currentTimeMillis()
        totalRecordingTimeMs = 0
        compressionNeeded = false
        audioBuffer.clear()

        CoroutineScope(Dispatchers.IO).launch {
            // Load model first
            try {
                modelManager.loadModel()
                Log.d("AudioTranscriptRepo", "Speech-to-text model loaded successfully")
            } catch (e: Exception) {
                Log.e("AudioTranscriptRepo", "Failed to load model", e)
                isListening = false
                return@launch
            }

            val sampleRate = 16000
            val minBuf = AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            if (minBuf <= 0) {
                Log.e("AudioTranscriptRepo", "Invalid min buffer size: $minBuf")
                isListening = false
                return@launch
            }

            try {
                val audioFormat = AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                    .build()

                // CHANGE 1: Use better audio source
                val audioSource = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                        try {
                            MediaRecorder.AudioSource.UNPROCESSED
                        } catch (e: Exception) {
                            MediaRecorder.AudioSource.VOICE_RECOGNITION
                        }
                    }
                    else -> MediaRecorder.AudioSource.VOICE_RECOGNITION
                }

                audioRecord = AudioRecord.Builder()
                    .setAudioSource(audioSource) // CHANGED from MIC
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(minBuf * 4) // CHANGED: Increased buffer
                    .build()
            } catch (se: SecurityException) {
                Log.e("AudioTranscriptRepo", "SecurityException creating AudioRecord", se)
                isListening = false
                return@launch
            } catch (e: Exception) {
                Log.e("AudioTranscriptRepo", "Error creating AudioRecord", e)
                isListening = false
                return@launch
            }

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e("AudioTranscriptRepo", "AudioRecord not initialized")
                stopListening()
                return@launch
            }

            try {
                audioRecord?.startRecording()
            } catch (ise: IllegalStateException) {
                Log.e("AudioTranscriptRepo", "startRecording failed", ise)
                stopListening()
                return@launch
            } catch (se: SecurityException) {
                Log.e("AudioTranscriptRepo", "startRecording SecurityException", se)
                stopListening()
                return@launch
            }

            listeningJob = launch {
                val buffer = ShortArray(minBuf)
                val sampleWindow = 16000 * 2
                val rollingBuffer = ShortArray(sampleWindow)
                var filled = 0

                var lastTokens: List<Int>? = null
                var stableText = ""
                var lastPending = ""

                // ADD: Silence tracking
                var silenceFrames = 0
                val maxSilenceFrames = 10

                while (isListening && isActive) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        // Store original buffer for audio file (before amplification)
                        val bufferCopy = buffer.copyOf(read)

                        // FIXED: Add buffer safely using thread-safe collection
                        audioBuffer.add(bufferCopy)

                        // CHANGE 2: Amplify audio for STT processing
                        val amplifiedBuffer = amplifyAudio(buffer, gain = 3.0f)

                        // Check total recording time
                        val currentTime = System.currentTimeMillis()
                        totalRecordingTimeMs = currentTime - recordingStartTime

                        // Check if we need file compression due to size
                        if (audioBuffer.size >= maxBufferSizeBeforeCompression) {
                            compressionNeeded = true
                            Log.d("AudioTranscriptRepo", "Buffer size limit reached, will use compression")
                        }

                        // Check if we've been recording for 1 minute OR reached buffer limit
                        if (totalRecordingTimeMs >= maxRecordingDurationMs || compressionNeeded) {
                            Log.d("AudioTranscriptRepo", "Triggering API analysis - Time: ${totalRecordingTimeMs}ms, Buffer size: ${audioBuffer.size}")
                            launch(Dispatchers.IO) {
                                sendAudioToAPI(shouldContinueListening = true)
                            }
                            // Reset for next batch
                            launch(Dispatchers.IO) {
                                resetAudioBufferForContinuousMode()
                            }
                        }

                        // Stop if we've exceeded maximum total recording time
                        if (totalRecordingTimeMs >= maxTotalRecordingTimeMs) {
                            Log.d("AudioTranscriptRepo", "Maximum total recording time reached, stopping")
                            return@launch
                        }

                        // CHANGE 3: Skip silent frames
                        if (!isSpeech(amplifiedBuffer)) {
                            silenceFrames++
                            if (silenceFrames > maxSilenceFrames) {
                                delay(50)
                                continue
                            }
                        } else {
                            silenceFrames = 0
                        }

                        val framesPerSecond = 16000f / 320f
                        val overlapFrames = (framesPerSecond * 1).toInt()

                        val spaceLeft = sampleWindow - filled
                        val copyCount = minOf(spaceLeft, read)
                        System.arraycopy(amplifiedBuffer, 0, rollingBuffer, filled, copyCount)
                        filled += copyCount

                        if (filled >= sampleWindow) {
                            val maxAmp = amplifiedBuffer.maxOf { kotlin.math.abs(it.toInt()).toFloat() } / 32768f
                            Log.d("AudioTranscriptRepo", "Amplified max amplitude: $maxAmp, RMS: ${calculateRMS(amplifiedBuffer)}")

                            val result = withContext(Dispatchers.Default) {
                                runCatching { modelManager.transcribeWithTokens(rollingBuffer.copyOf()) }
                                    .onFailure { Log.e("AudioTranscriptRepo", "Transcription error", it) }
                                    .getOrDefault(TranscriptionResult("", emptyList(), emptyArray()))
                            }

                            if (lastTokens == null) {
                                lastPending = result.text
                                _pendingChunk.postValue(lastPending)
                                _stableTranscript.postValue(stableText)
                            } else {
                                val dropCount = findFirstBlankAfterOverlap(result.tokens, overlapFrames, modelManager.blankTokenId)
                                val trimmedLogits = result.logitsArray.drop(dropCount).toTypedArray()

                                val newText = if (modelManager.languageModel != null) {
                                    modelManager.ctcBeamSearchWithLM(
                                        logits = trimmedLogits,
                                        beamWidth = 20,
                                        topK = 10,
                                        blankId = modelManager.blankTokenId,
                                        lmWeight = 0.5,
                                        wordInsertionPenalty = -0.5
                                    )
                                } else {
                                    modelManager.ctcBeamSearch(
                                        logits = trimmedLogits,
                                        beamWidth = 15,
                                        topK = 8
                                    )
                                }

                                if (lastPending.isNotBlank()) {
                                    stableText = (stableText + " " + lastPending).trim()
                                    _stableTranscript.postValue(stableText)
                                }

                                lastPending = newText
                                _pendingChunk.postValue(lastPending)

                                // Check for scam keywords in the new text
                                if (!scamFlagTriggered && containsScamKeyword(newText)) {
                                    scamFlagTriggered = true
                                    Log.d("AudioTranscriptRepo", "Scam keyword detected: $newText")

                                    launch(Dispatchers.IO) {
                                        sendAudioToAPI(shouldContinueListening = true)
                                    }
                                    launch(Dispatchers.IO) {
                                        resetAudioBufferForContinuousMode()
                                    }
                                }
                            }

                            lastTokens = result.tokens
                            val overlap = 4800
                            System.arraycopy(rollingBuffer, overlap, rollingBuffer, 0, overlap)
                            filled = overlap
                        }
                    }
                }
            }
        }
    }

    // FIXED: Thread-safe WAV file creation with proper synchronization
    private suspend fun createProperWavFile(): File? {
        return audioBufferMutex.withLock {
            try {
                val tempFile = createTempAudioFile()

                // Create a snapshot of the current buffer to avoid concurrent modification
                val bufferSnapshot = audioBuffer.toList()

                if (bufferSnapshot.isEmpty()) {
                    Log.w("AudioTranscriptRepo", "No audio data to save")
                    return null
                }

                // Calculate total samples and data length
                val totalSamples = bufferSnapshot.sumOf { it.size }
                val totalDataLen = totalSamples * 2 // 2 bytes per 16-bit sample

                tempFile.outputStream().use { out ->
                    // Write WAV header
                    writeWavHeaderToStream(out, totalDataLen)

                    // Write audio data from snapshot
                    bufferSnapshot.forEach { buffer ->
                        buffer.forEach { sample ->
                            // Write as little-endian 16-bit
                            out.write(sample.toInt() and 0xFF)
                            out.write((sample.toInt() shr 8) and 0xFF)
                        }
                    }
                }

                Log.d("AudioTranscriptRepo", "Created WAV file: ${tempFile.absolutePath}, size: ${tempFile.length()} bytes, samples: $totalSamples")
                tempFile
            } catch (e: Exception) {
                Log.e("AudioTranscriptRepo", "Error creating WAV file", e)
                null
            }
        }
    }

    private fun writeWavHeaderToStream(out: java.io.OutputStream, dataLength: Int) {
        val sampleRate = 16000
        val channels = 1
        val bitsPerSample = 16
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8
        val totalLength = dataLength + 36

        // RIFF header
        out.write("RIFF".toByteArray())
        out.write(intToLittleEndian(totalLength))
        out.write("WAVE".toByteArray())

        // fmt subchunk
        out.write("fmt ".toByteArray())
        out.write(intToLittleEndian(16)) // Subchunk1Size (16 for PCM)
        out.write(shortToLittleEndian(1)) // AudioFormat (1 = PCM)
        out.write(shortToLittleEndian(channels))
        out.write(intToLittleEndian(sampleRate))
        out.write(intToLittleEndian(byteRate))
        out.write(shortToLittleEndian(blockAlign))
        out.write(shortToLittleEndian(bitsPerSample))

        // data subchunk
        out.write("data".toByteArray())
        out.write(intToLittleEndian(dataLength))
    }

    private fun intToLittleEndian(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte(),
            ((value shr 16) and 0xFF).toByte(),
            ((value shr 24) and 0xFF).toByte()
        )
    }

    private fun shortToLittleEndian(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte()
        )
    }

    // FIXED: Thread-safe file saving
    private suspend fun saveAudioBufferToFile(): File? {
        if (audioBuffer.isEmpty()) {
            Log.w("AudioTranscriptRepo", "No audio data to save")
            return null
        }
        return createProperWavFile()
    }

    private suspend fun sendAudioToAPI(shouldContinueListening: Boolean = false) {
        try {
            Log.d("AudioTranscriptRepo", "Preparing to send audio to API, buffer size: ${audioBuffer.size} chunks, continue: $shouldContinueListening")

            val audioFile = if (compressionNeeded || totalRecordingTimeMs > 120_000) {
                createCompressedAudioFile()
            } else {
                saveAudioBufferToFile()
            }

            if (audioFile != null && audioFile.exists() && audioFile.length() > 44) {
                val mimeType = if (audioFile.extension == "mp4") "audio/mp4" else "audio/wav"
                val requestFile = audioFile.asRequestBody(mimeType.toMediaTypeOrNull())
                val audioMultipart = MultipartBody.Part.createFormData("audio_file", audioFile.name, requestFile)

                Log.d("AudioTranscriptRepo", "Sending ${audioFile.extension.uppercase()} file to API: ${audioFile.name}, size: ${audioFile.length()} bytes")
                val response = ApiManager.scamDetectionApi.analyzeAudioFile(audioMultipart)

                Log.d("AudioTranscriptRepo", "API Response - Risk: ${response.risk_level}, Confidence: ${response.confidence}")

                when (response.risk_level.lowercase()) {
                    "high" -> {
                        // High risk: Stop listening and show warning
                        globalStateManager.setScamResultData(
                            ScamResultData.ScamAnalysis(response)
                        )
                        delay(100)
                        stopListening()
                        _scamDetected.postValue(true)
                        Log.d("AudioTranscriptRepo", "HIGH RISK detected - stopping monitoring")
                    }
                    "medium" -> {
                        // Medium risk: Continue listening but increase monitoring
                        Log.d("AudioTranscriptRepo", "MEDIUM RISK detected - continuing monitoring with increased sensitivity")
                        if (shouldContinueListening) {
                            // Reduce the time threshold for next check
                            maxRecordingDurationMs = 30_000L // Check every 30 seconds instead of 60
                            scamFlagTriggered = false // Allow immediate re-triggering
                        }
                        // Don't stop listening, just continue
                    }
                    "low", "none" -> {
                        // Low/No risk: Continue normal monitoring
                        Log.d("AudioTranscriptRepo", "LOW/NO RISK detected - continuing normal monitoring")
                        if (shouldContinueListening) {
                            scamFlagTriggered = false
                        }
                        // Don't stop listening
                    }
                    else -> {
                        Log.w("AudioTranscriptRepo", "Unknown risk level: ${response.risk_level}")
                        scamFlagTriggered = false
                    }
                }

                // Clean up the temporary file
                audioFile.delete()
            } else {
                Log.e("AudioTranscriptRepo", "Failed to create valid audio file or file too small")
                scamFlagTriggered = false
            }
        } catch (e: Exception) {
            Log.e("AudioTranscriptRepo", "Error sending audio to API", e)
            scamFlagTriggered = false
        }
    }

    override fun stopListening() {
        isListening = false
        listeningJob?.cancel()
        listeningJob = null
        runCatching { audioRecord?.stop() }
        runCatching { audioRecord?.release() }
        audioRecord = null
        audioBuffer.clear()

        // Clean up any temporary files
        recordingFile?.let { file ->
            if (file.exists()) {
                file.delete()
            }
        }
        recordingFile = null
    }

    private fun containsScamKeyword(text: String): Boolean {
        val lowerText = text.lowercase(Locale.getDefault())
        return scamKeywords.any { keyword ->
            lowerText.contains(keyword.lowercase(Locale.getDefault()))
        }
    }

    private fun findFirstBlankAfterOverlap(tokens: List<Int>, overlapFrames: Int, blankId: Int): Int {
        for (i in overlapFrames until tokens.size) {
            if (tokens[i] == blankId) {
                return i + 1
            }
        }
        return overlapFrames
    }

    private suspend fun createCompressedAudioFile(): File? {
        return try {
            val tempFile = if (compressionNeeded || totalRecordingTimeMs > 120_000) { // 2+ minutes
                createTempMp4File()
            } else {
                createTempAudioFile() // WAV for shorter recordings
            }

            if (tempFile.extension == "mp4") {
                Log.d("AudioTranscriptRepo", "Creating compressed MP4 file due to size/duration")
                createMp4File(tempFile)
            } else {
                Log.d("AudioTranscriptRepo", "Creating WAV file for shorter recording")
                createProperWavFile()
            }
        } catch (e: Exception) {
            Log.e("AudioTranscriptRepo", "Error creating audio file", e)
            null
        }
    }

    private fun createTempMp4File(): File {
        val tempDir = File(context.cacheDir, "audio_recordings")
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        return File(tempDir, "temp_recording_${System.currentTimeMillis()}.mp4")
    }

    private suspend fun createMp4File(outputFile: File): File? {
        return try {
            // First create a WAV file
            val wavFile = createProperWavFile() ?: return null

            Log.d("AudioTranscriptRepo", "Using WAV chunking strategy instead of MP4")
            wavFile

        } catch (e: Exception) {
            Log.e("AudioTranscriptRepo", "Error creating MP4 file", e)
            createProperWavFile() // Fallback to WAV
        }
    }

    // FIXED: Thread-safe buffer reset with mutex protection
    private suspend fun resetAudioBufferForContinuousMode() {
        audioBufferMutex.withLock {
            // Keep some overlap for context, but reduce buffer size
            if (audioBuffer.size > 10) {
                val keepLast = audioBuffer.takeLast(5) // Keep last 5 chunks for context
                audioBuffer.clear()
                audioBuffer.addAll(keepLast)
            }
            compressionNeeded = false
            recordingStartTime = System.currentTimeMillis() // Reset timer for next batch
            scamFlagTriggered = false // Allow next detection
        }
    }

    override fun resetScamDetection() {
        globalStateManager.clearScamResultData()
    }
}