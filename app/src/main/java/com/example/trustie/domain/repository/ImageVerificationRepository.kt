package com.example.trustie.domain.repository

import android.net.Uri
import com.example.trustie.data.remote.dto.ImageVerificationResponse

interface ImageVerificationRepository {
    suspend fun verifyImage(imageUri: Uri, userId: Int, description: String?): Result<ImageVerificationResponse>
}
