package com.example.trustie.repository.audiotranscriptrepo

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioTranscriptRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioTranscriptRepository {

    private val _transcript = MutableLiveData<String>()
    override val transcript: LiveData<String> get() = _transcript

    private val _scamDetected = MutableLiveData<Boolean>()
    override val scamDetected: LiveData<Boolean> get() = _scamDetected

    private var audioRecord: AudioRecord? = null
    private var isListening = false
    private var listeningJob: Job? = null

    // Example scam keywords
    private val scamKeywords = listOf("bank account", "password", "OTP", "transfer", "credit card")

    init {
        System.loadLibrary("whisper") // JNI lib from whisper.cpp
        nativeInitModel("${context.filesDir}/models/ggml-base.en.bin")
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun startListening() {
        if (isListening) return
        isListening = true

        val sampleRate = 16000
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioRecord?.startRecording()

        listeningJob = CoroutineScope(Dispatchers.IO).launch {
            val audioBuffer = ShortArray(bufferSize)

            while (isListening && isActive) {
                val read = audioRecord?.read(audioBuffer, 0, audioBuffer.size) ?: 0
                if (read > 0) {
                    val transcriptChunk = withContext(Dispatchers.Default) {
                        whisperTranscribeChunk(audioBuffer, read)
                    }

                    if (!transcriptChunk.isNullOrEmpty()) {
                        withContext(Dispatchers.Main) {
                            _transcript.value = (_transcript.value ?: "") + " " + transcriptChunk

                            if (containsScamKeyword(transcriptChunk)) {
                                _scamDetected.value = true
                                sendToLLM(_transcript.value ?: "")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun stopListening() {
        isListening = false
        listeningJob?.cancel()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    private fun containsScamKeyword(text: String): Boolean {
        val lowerText = text.lowercase(Locale.getDefault())
        return scamKeywords.any { keyword -> lowerText.contains(keyword.lowercase(Locale.getDefault())) }
    }

    // Placeholder for sending conversation to LLM
    private fun sendToLLM(conversation: String) {
        // TODO: Implement API call to GPT or your LLM
    }

    // JNI native calls
    private external fun nativeInitModel(modelPath: String)
    private external fun whisperTranscribeChunk(audioData: ShortArray, length: Int): String
}
