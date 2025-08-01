package com.example.trustie.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.repository.AuthRepository
import com.example.trustie.ui.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val context: Context
) : ViewModel() {

    private val repository = AuthRepository(context)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _otpSent = MutableStateFlow(false)
    val otpSent: StateFlow<Boolean> = _otpSent.asStateFlow()

    init {
        Log.d("AuthDebug", "AuthViewModel initialized")
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        _isLoggedIn.value = repository.isLoggedIn()
        _currentUser.value = repository.getCurrentUser()
        Log.d("AuthDebug", "Login status checked: ${_isLoggedIn.value}")
    }

    fun updatePhoneNumber(phone: String) {
        _phoneNumber.value = phone
        Log.d("AuthDebug", "Phone number updated: $phone")
    }

    fun updateOTPCode(otp: String) {
        if (otp.length <= 4) { // Giới hạn 4 ký tự
            _otpCode.value = otp
            Log.d("AuthDebug", "OTP code updated: $otp")
        }
    }

    fun sendOTP() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                Log.d("AuthDebug", "Sending OTP...")
                val response = repository.sendOTP(_phoneNumber.value)

                if (response.success) {
                    _otpSent.value = true
                    _successMessage.value = response.message
                    Log.d("AuthDebug", "OTP sent successfully")
                } else {
                    _errorMessage.value = response.message ?: "Không thể gửi mã OTP"
                    Log.e("AuthDebug", "Failed to send OTP: ${response.message}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
                Log.e("AuthDebug", "Exception sending OTP: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun verifyOTP() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                Log.d("AuthDebug", "Verifying OTP...")
                val response = repository.verifyOTP(_phoneNumber.value, _otpCode.value)

                if (response.success && response.user != null) {
                    _currentUser.value = response.user
                    _isLoggedIn.value = true
                    _successMessage.value = response.message
                    Log.d("AuthDebug", "OTP verified successfully")
                } else {
                    _errorMessage.value = response.message ?: "Mã OTP không chính xác"
                    Log.e("AuthDebug", "Failed to verify OTP: ${response.message}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
                Log.e("AuthDebug", "Exception verifying OTP: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
        _isLoggedIn.value = false
        _phoneNumber.value = ""
        _otpCode.value = ""
        _otpSent.value = false
        clearMessages()
        Log.d("AuthDebug", "User logged out")
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun resetOTPFlow() {
        _otpCode.value = ""
        _otpSent.value = false
        clearMessages()
        Log.d("AuthDebug", "OTP flow reset")
    }
}
