package com.example.trustie.repository.imagerepo

import android.util.Log
import com.example.trustie.data.model.request.ImageVerificationRequest
import com.example.trustie.data.model.response.ImageVerificationResponse
import com.example.trustie.data.model.response.Entities
import com.example.trustie.data.model.response.LlmAnalysis
import kotlinx.coroutines.delay
import javax.inject.Inject

class ImageVerificationRepositoryImpl @Inject constructor() : ImageVerificationRepository {

    override suspend fun verifyImage(request: ImageVerificationRequest): Result<ImageVerificationResponse> {
        return try {
            Log.d("ImageRepoDebug", "Starting image verification for userId: ${request.userId}")
            
            // Simulate API delay
            delay(2000)
            
            // Mock response based on the request
            val mockResponse = createMockResponse(request)
            
            Log.d("ImageRepoDebug", "Image verification completed successfully")
            Result.success(mockResponse)
        } catch (e: Exception) {
            Log.e("ImageRepoDebug", "Error during image verification: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    private fun createMockResponse(request: ImageVerificationRequest): ImageVerificationResponse {
        // Create a mock response based on the request
        val isSuspicious = request.description?.contains("suspicious", ignoreCase = true) == true
        
        return ImageVerificationResponse(
            screenshotId = 1,
            ocrText = "Sample OCR text from image",
            entities = Entities(
                phones = listOf("0123456789"),
                urls = listOf("https://example.com"),
                emails = listOf("test@example.com")
            ),
            llmAnalysis = LlmAnalysis(
                analysis = "This image appears to be ${if (isSuspicious) "suspicious" else "safe"}",
                riskLevel = if (isSuspicious) "HIGH" else "LOW",
                confidence = 85,
                modelUsed = "GPT-4"
            )
        )
    }
}