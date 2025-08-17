package com.example.trustie.repository.ttsrepo

import android.content.Context
import android.util.Log
import com.example.trustie.data.api.ApiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
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

            // Call API to get audio file
            val response = ApiManager.textToSpeechApi.textToSpeech(text)

            // Ensure success
            if (!response.isSuccessful) {
                return Result.failure(Exception("TTS request failed: ${response.code()}"))
            }

            // Get the response body
            val responseBody = response.body()
            if (responseBody == null) {
                return Result.failure(Exception("Empty response body"))
            }

            // Get content length for progress tracking
            val contentLength = responseBody.contentLength()
            Log.d("TextToSpeechRepository", "Audio file size: $contentLength bytes")

            // Save to local storage
            val fileName = "tts_${System.currentTimeMillis()}.wav"
            val file = File(context.cacheDir, fileName)
            
            // Use FileOutputStream to write the audio data
            FileOutputStream(file).use { outputStream ->
                responseBody.byteStream().use { inputStream ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalBytesRead = 0L
                    
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        
                        // Log progress for large files
                        if (contentLength > 0 && totalBytesRead % (contentLength / 10) == 0L) {
                            val progress = (totalBytesRead * 100 / contentLength).toInt()
                            Log.d("TextToSpeechRepository", "Download progress: $progress%")
                        }
                    }
                }
            }

            Log.d("TextToSpeechRepository", "Audio saved to: ${file.absolutePath}, size: ${file.length()} bytes")
            
            // Verify file was created and has content
            if (!file.exists() || file.length() == 0L) {
                return Result.failure(Exception("Failed to save audio file or file is empty"))
            }

            Result.success(file.absolutePath)

        } catch (e: Exception) {
            Log.e("TextToSpeechRepository", "Error converting text to speech", e)
            Result.failure(e)
        }
    }
} 