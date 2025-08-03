package com.example.trustie.data.model

data class PhoneCheckItem(
    val phoneNumber: String,
    val isSuspicious: Boolean,
    val riskLevel: String,
    val reportCount: Int,
    val lastReported: String?,
    val description: String?
)

data class PhoneCheckResponse(
    val success: Boolean,
    val data: PhoneCheckItem?,
    val message: String? = null
)