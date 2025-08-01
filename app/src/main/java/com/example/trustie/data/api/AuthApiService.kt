package com.example.trustie.data.api

import com.example.trustie.ui.model.AuthResponse
import com.example.trustie.ui.model.LoginRequest
import com.example.trustie.ui.model.OTPRequest
import kotlinx.coroutines.delay

class AuthApiService {
    // Base URL for your backend API
    private val baseUrl = "https://your-api-domain.com/api/"

    suspend fun sendOTP(request: LoginRequest): AuthResponse {
        // Simulate API call
        delay(1500)
        throw NotImplementedError("Implement actual API call here")
    }

    suspend fun verifyOTP(request: OTPRequest): AuthResponse {
        // Simulate API call
        delay(1500)
        throw NotImplementedError("Implement actual API call here")
    }

    suspend fun refreshToken(token: String): AuthResponse {
        // Simulate API call
        delay(1000)
        throw NotImplementedError("Implement actual API call here")
    }
}
