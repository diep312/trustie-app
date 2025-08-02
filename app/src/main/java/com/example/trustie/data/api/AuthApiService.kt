package com.example.trustie.data.api

import com.example.trustie.ui.model.AuthResponse
import com.example.trustie.ui.model.LoginRequest
import com.example.trustie.ui.model.OTPRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/v1/auth/send-otp")
    suspend fun sendOtp(@Body request: LoginRequest): AuthResponse

    @POST("api/v1/auth/verify-otp")
    suspend fun verifyOtp(@Body request: OTPRequest): AuthResponse
}
