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
            val response = ApiManager.textToSpeechApi.textToSpeech(text)
            Log.d("TextToSpeechRepository", "TTS response: $response")
            
            // Extract audio URL from response
            val audioUrl = response["audio_url"] as? String
            if (audioUrl != null) {
                // Download the audio file
                val localFilePath = downloadAudioFile(audioUrl).getOrThrow()
                Result.success(localFilePath)
            } else {
                Result.failure(Exception("No audio URL in response"))
            }
        } catch (e: Exception) {
            Log.e("TextToSpeechRepository", "Error converting text to speech", e)
            Result.failure(e)
        }
    }
    
    override suspend fun downloadAudioFile(audioUrl: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("TextToSpeechRepository", "Downloading audio file from: $audioUrl")
                
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(audioUrl)
                    .build()
                
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    throw Exception("Failed to download audio file: ${response.code}")
                }
                
                // Create a temporary file in the app's cache directory
                val audioFile = File(context.cacheDir, "tts_audio_${System.currentTimeMillis()}.wav")
                val inputStream = response.body?.byteStream()
                
                inputStream?.use { input ->
                    FileOutputStream(audioFile).use { output ->
                        input.copyTo(output)
                    }
                }
                
                Log.d("TextToSpeechRepository", "Audio file saved to: ${audioFile.absolutePath}")
                Result.success(audioFile.absolutePath)
            } catch (e: Exception) {
                Log.e("TextToSpeechRepository", "Error downloading audio file", e)
                Result.failure(e)
            }
        }
    }
} 