package com.example.trustie.data.api

import com.example.trustie.data.model.AuthRequest
import com.example.trustie.data.model.datamodel.User
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/send-otp")
    suspend fun sendOtp(@Body request: AuthRequest): User

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: AuthRequest): User
}
