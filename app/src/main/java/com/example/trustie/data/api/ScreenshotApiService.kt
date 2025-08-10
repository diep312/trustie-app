package com.example.trustie.data.api

import com.example.trustie.data.model.response.ImageVerificationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ScreenshotApiService {
    @Multipart
    @POST("screenshot/analyze")
    suspend fun analyzeScreenshot(
        @Part file: MultipartBody.Part,
        @Part("user_id") userId: RequestBody,
        @Part("description") description: RequestBody?
    ): ImageVerificationResponse
}
