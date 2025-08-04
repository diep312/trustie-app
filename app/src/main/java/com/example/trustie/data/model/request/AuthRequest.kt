package com.example.trustie.data.model

import com.example.trustie.data.model.datamodel.User

data class AuthRequest(
    val phoneNumber: String,
    val otp: String? = null
)

data class LoginRequest(
    val phoneNumber: String
)

data class OTPRequest(
    val phoneNumber: String,
    val otp: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String? = null,
    val user: User? = null,
    val token: String? = null,
    val otpSent: Boolean = false
)
