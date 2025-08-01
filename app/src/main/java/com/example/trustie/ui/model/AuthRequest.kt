package com.example.trustie.ui.model

data class LoginRequest(
    val phoneNumber: String
)

data class OTPRequest(
    val phoneNumber: String,
    val otpCode: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String? = null,
    val user: User? = null,
    val token: String? = null,
    val otpSent: Boolean = false
)
