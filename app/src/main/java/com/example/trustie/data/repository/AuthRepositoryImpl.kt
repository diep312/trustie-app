//package com.example.trustie.data.repository
//
//import android.content.Context
//import android.content.SharedPreferences
//import android.util.Log
//import com.example.trustie.data.api.AuthApiService
//import com.example.trustie.domain.repository.AuthRepository
//import com.example.trustie.ui.model.AuthResponse
//import com.example.trustie.ui.model.LoginRequest
//import com.example.trustie.ui.model.OTPRequest
//import com.example.trustie.ui.model.User
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class AuthRepositoryImpl @Inject constructor(
//    private val context: Context,
//    private val apiService: AuthApiService // Hilt sẽ inject AuthApiService (là interface Retrofit)
//) : AuthRepository {
//
//    private val sharedPrefs: SharedPreferences =
//        context.getSharedPreferences("trustie_auth", Context.MODE_PRIVATE)
//
//    private val USER_ID_KEY = "user_id"
//    private val PHONE_NUMBER_KEY = "phone_number"
//    private val USER_NAME_KEY = "user_name"
//    private val AUTH_TOKEN_KEY = "auth_token"
//    private val IS_LOGGED_IN_KEY = "is_logged_in"
//
//    override suspend fun sendOTP(phoneNumber: String): AuthResponse {
//        return try {
//            Log.d("AuthDebug", "Sending OTP to: $phoneNumber via API")
//            val response = apiService.sendOtp(LoginRequest(phoneNumber = phoneNumber))
//            Log.d("AuthDebug", "OTP API response: $response")
//            response
//        } catch (e: Exception) {
//            Log.e("AuthDebug", "Error sending OTP: ${e.message}", e)
//            AuthResponse(
//                success = false,
//                message = "Lỗi kết nối khi gửi OTP: ${e.message}"
//            )
//        }
//    }
//
//    override suspend fun verifyOTP(phoneNumber: String, otpCode: String): AuthResponse {
//        return try {
//            Log.d("AuthDebug", "Verifying OTP: $otpCode for phone: $phoneNumber via API")
//            val response = apiService.verifyOtp(
//                OTPRequest(
//                    phoneNumber = phoneNumber,
//                    otp = otpCode
//                )
//            )
//            Log.d("AuthDebug", "Verify OTP API response: $response")
//
//            if (response.success && response.user != null && response.token != null) {
//                saveUserSession(response.user, response.token)
//            }
//            response
//        } catch (e: Exception) {
//            Log.e("AuthDebug", "Error verifying OTP: ${e.message}", e)
//            AuthResponse(
//                success = false,
//                message = "Lỗi kết nối khi xác thực OTP: ${e.message}"
//            )
//        }
//    }
//
//    override fun saveUserSession(user: User, token: String) {
//        sharedPrefs.edit().apply {
//            putInt(USER_ID_KEY, user.id)
//            putString(PHONE_NUMBER_KEY, user.phoneNumber)
//            putString(USER_NAME_KEY, user.name)
//            putString(AUTH_TOKEN_KEY, token)
//            putBoolean(IS_LOGGED_IN_KEY, true)
//            apply()
//        }
//        Log.d("AuthDebug", "User session saved: userId=${user.id}, phone=${user.phoneNumber}")
//    }
//
//    override fun isLoggedIn(): Boolean {
//        return sharedPrefs.getBoolean(IS_LOGGED_IN_KEY, false)
//    }
//
//    override fun getCurrentUser(): User? {
//        return if (isLoggedIn()) {
//            User(
//                id = sharedPrefs.getInt(USER_ID_KEY, -1),
//                phoneNumber = sharedPrefs.getString(PHONE_NUMBER_KEY, "") ?: "",
//                name = sharedPrefs.getString(USER_NAME_KEY, ""),
//                isVerified = true
//            )
//        } else {
//            null
//        }
//    }
//
//    override fun getUserId(): Int? {
//        val id = sharedPrefs.getInt(USER_ID_KEY, -1)
//        return if (id != -1) id else null
//    }
//
//    override fun logout() {
//        sharedPrefs.edit().clear().apply()
//        Log.d("AuthDebug", "User logged out and session cleared.")
//    }
//}

package com.example.trustie.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.trustie.data.api.AuthApiService
import com.example.trustie.domain.repository.AuthRepository
import com.example.trustie.ui.model.AuthResponse
import com.example.trustie.ui.model.LoginRequest
import com.example.trustie.ui.model.OTPRequest
import com.example.trustie.ui.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val context: Context,
    private val apiService: AuthApiService
) : AuthRepository {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("trustie_auth", Context.MODE_PRIVATE)

    private val USER_ID_KEY = "user_id"
    private val PHONE_NUMBER_KEY = "phone_number"
    private val USER_NAME_KEY = "user_name"
    private val AUTH_TOKEN_KEY = "auth_token"
    private val IS_LOGGED_IN_KEY = "is_logged_in"

    override suspend fun sendOTP(phoneNumber: String): AuthResponse {
        return try {
            Log.d("AuthDebug", "Sending OTP to: $phoneNumber via API")
            val response = apiService.sendOtp(LoginRequest(phoneNumber = phoneNumber))
            Log.d("AuthDebug", "OTP API response: $response")
            response
        } catch (e: Exception) {
            Log.e("AuthDebug", "Error sending OTP: ${e.message}", e)
            AuthResponse(
                success = false,
                message = "Lỗi kết nối khi gửi OTP: ${e.message}"
            )
        }
    }

    override suspend fun verifyOTP(phoneNumber: String, otpCode: String): AuthResponse {
        // --- BẮT ĐẦU PHẦN THAY ĐỔI CHO MỤC ĐÍCH TEST ---
        if (otpCode == "1234") { // Kiểm tra nếu OTP là "1234"
            Log.d("AuthDebug", "OTP '1234' accepted for testing purposes.")
            // Mô phỏng một phản hồi thành công
            val mockUser = User(id = 1, phoneNumber = phoneNumber, name = "Test User", isVerified = true)
            val mockToken = "mock_test_token_1234"
            saveUserSession(mockUser, mockToken) // Lưu session giả lập
            return AuthResponse(
                success = true,
                message = "Xác thực OTP thành công (test mode)",
                user = mockUser,
                token = mockToken
            )
        }
        // --- KẾT THÚC PHẦN THAY ĐỔI CHO MỤC ĐÍCH TEST ---

        // Nếu không phải OTP test, tiếp tục gọi API thực tế
        return try {
            Log.d("AuthDebug", "Verifying OTP: $otpCode for phone: $phoneNumber via API")
            val response = apiService.verifyOtp(OTPRequest(phoneNumber = phoneNumber, otp = otpCode))
            Log.d("AuthDebug", "Verify OTP API response: $response")

            if (response.success && response.user != null && response.token != null) {
                saveUserSession(response.user, response.token)
            }
            response
        } catch (e: Exception) {
            Log.e("AuthDebug", "Error verifying OTP: ${e.message}", e)
            AuthResponse(
                success = false,
                message = "Lỗi kết nối khi xác thực OTP: ${e.message}"
            )
        }
    }

    override fun saveUserSession(user: User, token: String) {
        sharedPrefs.edit().apply {
            putInt(USER_ID_KEY, user.id)
            putString(PHONE_NUMBER_KEY, user.phoneNumber)
            putString(USER_NAME_KEY, user.name)
            putString(AUTH_TOKEN_KEY, token)
            putBoolean(IS_LOGGED_IN_KEY, true)
            apply()
        }
        Log.d("AuthDebug", "User session saved: userId=${user.id}, phone=${user.phoneNumber}")
    }

    override fun isLoggedIn(): Boolean {
        return sharedPrefs.getBoolean(IS_LOGGED_IN_KEY, false)
    }

    override fun getCurrentUser(): User? {
        return if (isLoggedIn()) {
            User(
                id = sharedPrefs.getInt(USER_ID_KEY, -1),
                phoneNumber = sharedPrefs.getString(PHONE_NUMBER_KEY, "") ?: "",
                name = sharedPrefs.getString(USER_NAME_KEY, ""),
                isVerified = true
            )
        } else {
            null
        }
    }

    override fun getUserId(): Int? {
        val id = sharedPrefs.getInt(USER_ID_KEY, -1)
        return if (id != -1) id else null
    }

    override fun logout() {
        sharedPrefs.edit().clear().apply()
        Log.d("AuthDebug", "User logged out and session cleared.")
    }
}

