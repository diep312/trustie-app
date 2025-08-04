package com.example.trustie.data.model.request

data class ImageVerificationRequest(
    val userId: Int,
    val description: String? = null,
    val imageUri: String? = null
) 