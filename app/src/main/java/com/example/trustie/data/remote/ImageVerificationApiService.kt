package com.example.trustie.data.remote

import com.example.trustie.data.remote.dto.ImageVerificationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageVerificationApiService {
    @Multipart
    @POST("/screenshot/analyze") // Thay thế bằng endpoint API thực tế của bạn
    suspend fun verifyImage(
        @Part file: MultipartBody.Part, // Tên trường là 'file'
        @Part("user_id") userId: RequestBody, // Tên trường là 'user_id'
        @Part("description") description: RequestBody? // Tên trường là 'description'
    ): ImageVerificationResponse
}
