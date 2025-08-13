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
import com.example.trustie.data.api.ApiManager
import com.example.trustie.data.local.wave2vec.OnnxWav2Vec2Manager
import com.example.trustie.data.local.wave2vec.TFLiteModelManager
import com.example.trustie.data.local.wave2vec.TranscriptionResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioTranscriptRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val modelManager: OnnxWav2Vec2Manager
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

    // Vietnamese scam keywords
    private val scamKeywords = listOf(
        "tài khoản ngân hàng",
        "mật khẩu",
        "otp",
        "chuyển khoản",
        "thẻ tín dụng",
        "lừa đảo",
        "nạp tiền",
        "vay tiền",
        "thanh toán"
    )

    // Flags to prevent repeated calls
    private var scamFlagTriggered = false

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun startListening() {
        if (isListening) return

        isListening = true
        scamFlagTriggered = false

        CoroutineScope(Dispatchers.IO).launch {
            // 3) Load model first
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

                audioRecord = AudioRecord.Builder()
                    .setAudioSource(MediaRecorder.AudioSource.MIC)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(minBuf * 2)
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
                val buffer = ShortArray(minBuf)               // mic read buffer
                val sampleWindow = 3200                     // 2 sec @ 16kHz
                val rollingBuffer = ShortArray(sampleWindow)  // sliding audio buffer
                var filled = 0

                while (isListening && isActive) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {

                        val framesPerSecond = 50 // ~20ms per frame with Wav2Vec2 base
                        val overlapFrames = framesPerSecond * 1 // 1 second overlap

                        var lastTokens: List<Int>? = null
                        var stableText = ""
                        var lastPending = ""


                        val spaceLeft = sampleWindow - filled
                        val copyCount = minOf(spaceLeft, read)
                        System.arraycopy(buffer, 0, rollingBuffer, filled, copyCount)
                        filled += copyCount



                        if (filled >= sampleWindow) {
                            val result = withContext(Dispatchers.Default) {
                                runCatching { modelManager.transcribeWithTokens(rollingBuffer.copyOf()) }
                                    .onFailure { Log.e("AudioTranscriptRepo", "Transcription error", it) }
                                    .getOrDefault(TranscriptionResult("", emptyList()))
                            }

                            if (lastTokens == null) {
                                // First chunk: nothing stable yet, entire thing is pending
                                lastPending = result.text
                                _pendingChunk.postValue(lastPending)
                                _stableTranscript.postValue(stableText)
                            } else {
                                // Drop overlap frames from this chunk
                                val newTokens = result.tokens.drop(overlapFrames)
                                val newText = modelManager.ctcDecode(newTokens.toIntArray())

                                // Commit previous pending into stable transcript
                                if (lastPending.isNotBlank()) {
                                    stableText = (stableText + " " + lastPending).trim()
                                    _stableTranscript.postValue(stableText)
                                }

                                // New text becomes the new pending chunk
                                lastPending = newText
                                _pendingChunk.postValue(lastPending)

                                // Scam keyword check only on committed text + pending
                                if (!scamFlagTriggered && containsScamKeyword(newText)) {
                                    scamFlagTriggered = true
                                    _scamDetected.postValue(true)
                                    launch(Dispatchers.IO) { sendToLLM((stableText + " " + newText).trim()) }
                                }
                            }

                            // Always update lastTokens
                            lastTokens = result.tokens

                            // Slide buffer for overlap
                            val overlap = sampleWindow / 2
                            System.arraycopy(rollingBuffer, overlap, rollingBuffer, 0, overlap)
                            filled = overlap
                        }
                    }
                }
            }
        }
    }

    override fun stopListening() {
        isListening = false
        listeningJob?.cancel()
        listeningJob = null
        runCatching { audioRecord?.stop() }
        runCatching { audioRecord?.release() }
        audioRecord = null
    }

    private fun containsScamKeyword(text: String): Boolean {
        val lowerText = text.lowercase(Locale.getDefault())
        return scamKeywords.any { keyword ->
            lowerText.contains(keyword.lowercase(Locale.getDefault()))
        }
    }

    private suspend fun sendToLLM(conversation: String): Map<String, Any> {
        return try {
            val response = ApiManager.scamDetectionApi.analyzeAudioTranscript(conversation)
            Log.d("AudioTranscriptRepository", "LLM Response: $response")
            response
        } catch (e: Exception) {
            Log.e("AudioTranscriptRepository", "Error sending to LLM", e)
            emptyMap()
        }
    }
}
