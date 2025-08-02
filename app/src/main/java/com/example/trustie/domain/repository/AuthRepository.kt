package com.example.trustie.domain.repository

import com.example.trustie.ui.model.AuthResponse
import com.example.trustie.ui.model.User

interface AuthRepository {
    suspend fun sendOTP(phoneNumber: String): AuthResponse
    suspend fun verifyOTP(phoneNumber: String, otpCode: String): AuthResponse
    fun saveUserSession(user: User, token: String)
    fun isLoggedIn(): Boolean
    fun getCurrentUser(): User?
    fun getUserId(): Int?
    fun logout()
}
