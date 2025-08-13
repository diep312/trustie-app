package com.example.trustie.repository.ttsrepo

import android.content.Context
import android.util.Log
import com.example.trustie.data.api.ApiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextToSpeechRepositoryImpl @Inject constructor(
    private val context: Context
) : TextToSpeechRepository {

    override suspend fun textToSpeech(text: String): Result<String> {
        return try {
            Log.d("TextToSpeechRepository", "Converting text to speech: $text")

            // Call API (this should return ResponseBody)
            val response = ApiManager.textToSpeechApi.textToSpeech(text)

            // Ensure success
            if (!response.isSuccessful) {
                return Result.failure(Exception("TTS request failed: ${response.code()}"))
            }

            // Get raw audio bytes
            val audioBytes = response.body()?.bytes()
            if (audioBytes == null || audioBytes.isEmpty()) {
                return Result.failure(Exception("Empty audio file in response"))
            }

            // Save to local storage
            val fileName = "tts_${System.currentTimeMillis()}.wav"
            val file = File(context.cacheDir, fileName)
            file.outputStream().use { it.write(audioBytes) }

            Log.d("TextToSpeechRepository", "Audio saved to: ${file.absolutePath}")
            Result.success(file.absolutePath)

        } catch (e: Exception) {
            Log.e("TextToSpeechRepository", "Error converting text to speech", e)
            Result.failure(e)
        }
    }
} 