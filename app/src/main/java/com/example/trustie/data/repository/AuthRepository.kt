package com.example.trustie.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.trustie.data.api.AuthApiService
import com.example.trustie.ui.model.AuthResponse
import com.example.trustie.ui.model.LoginRequest
import com.example.trustie.ui.model.OTPRequest
import com.example.trustie.ui.model.User
import kotlinx.coroutines.delay

class AuthRepository(
    private val context: Context,
    private val apiService: AuthApiService = AuthApiService()
) {
    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("trustie_auth", Context.MODE_PRIVATE)

    suspend fun sendOTP(phoneNumber: String): AuthResponse {
        return try {
            Log.d("AuthDebug", "Sending OTP to: $phoneNumber")
            delay(1500) // Mô phỏng độ trễ mạng

            // Dữ liệu giả để test UI
            if (phoneNumber.isNotEmpty() && phoneNumber.length >= 10) {
                AuthResponse(
                    success = true,
                    message = "Mã OTP đã được gửi đến số điện thoại $phoneNumber",
                    otpSent = true
                )
            } else {
                AuthResponse(
                    success = false,
                    message = "Số điện thoại không hợp lệ"
                )
            }
        } catch (e: Exception) {
            Log.e("AuthDebug", "Error sending OTP: ${e.message}", e)
            AuthResponse(
                success = false,
                message = "Lỗi kết nối: ${e.message}"
            )
        }
    }

    suspend fun verifyOTP(phoneNumber: String, otpCode: String): AuthResponse {
        return try {
            Log.d("AuthDebug", "Verifying OTP: $otpCode for phone: $phoneNumber")
            delay(1500) // Mô phỏng độ trễ mạng

            // Dữ liệu giả để test UI - OTP đúng là "1234"
            if (otpCode == "1234") {
                val mockUser = User(
                    id = "user_${System.currentTimeMillis()}",
                    phoneNumber = phoneNumber,
                    name = "Người dùng",
                    isVerified = true,
                    createdAt = "2023-10-26 10:00:00"
                )

                val mockToken = "mock_token_${System.currentTimeMillis()}"

                // Lưu thông tin đăng nhập
                saveUserSession(mockUser, mockToken)

                AuthResponse(
                    success = true,
                    message = "Đăng nhập thành công",
                    user = mockUser,
                    token = mockToken
                )
            } else {
                AuthResponse(
                    success = false,
                    message = "Mã OTP không chính xác"
                )
            }
        } catch (e: Exception) {
            Log.e("AuthDebug", "Error verifying OTP: ${e.message}", e)
            AuthResponse(
                success = false,
                message = "Lỗi kết nối: ${e.message}"
            )
        }
    }

    fun saveUserSession(user: User, token: String) {
        sharedPrefs.edit().apply {
            putString("user_id", user.id)
            putString("phone_number", user.phoneNumber)
            putString("user_name", user.name)
            putString("auth_token", token)
            putBoolean("is_logged_in", true)
            apply()
        }
        Log.d("AuthDebug", "User session saved")
    }

    fun isLoggedIn(): Boolean {
        return sharedPrefs.getBoolean("is_logged_in", false)
    }

    fun getCurrentUser(): User? {
        return if (isLoggedIn()) {
            User(
                id = sharedPrefs.getString("user_id", "") ?: "",
                phoneNumber = sharedPrefs.getString("phone_number", "") ?: "",
                name = sharedPrefs.getString("user_name", ""),
                isVerified = true
            )
        } else {
            null
        }
    }

    fun logout() {
        sharedPrefs.edit().clear().apply()
        Log.d("AuthDebug", "User logged out")
    }
}
