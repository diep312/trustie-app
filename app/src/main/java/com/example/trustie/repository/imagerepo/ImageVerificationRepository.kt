package com.example.trustie.repository.imagerepo

import com.example.trustie.data.model.request.ImageVerificationRequest
import com.example.trustie.data.model.response.ImageVerificationResponse

interface ImageVerificationRepository {
    suspend fun verifyImage(request: ImageVerificationRequest): Result<ImageVerificationResponse>
} 