package com.example.trustie.data.api

import okhttp3.MultipartBody
import retrofit2.http.*

interface ScreenshotApiService {
    
    @Multipart
    @POST("screenshot/analyze")
    suspend fun analyzeScreenshot(
        @Part file: MultipartBody.Part,
        @Part("user_id") userId: Int,
        @Part("description") description: String? = null
    ): Map<String, Any>
} 