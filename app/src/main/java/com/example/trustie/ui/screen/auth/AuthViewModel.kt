package com.example.trustie.ui.screen.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.model.datamodel.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val isOtpSent: Boolean = false,
    val currentUser: User? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    init {
        Log.d("AuthViewModel", "AuthViewModel initialized")
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        _authState.value = _authState.value.copy(
            isAuthenticated = authRepository.isLoggedIn(),
            currentUser = authRepository.getCurrentUser()
        )
        Log.d("AuthViewModel", "Initial login status: ${_authState.value.isAuthenticated}")
    }

    fun setPhoneNumber(number: String) {
        _phoneNumber.value = number
        _authState.value = _authState.value.copy(errorMessage = null, successMessage = null, isOtpSent = false)
        Log.d("AuthViewModel", "Phone number updated: $number")
    }

    fun setOtpCode(code: String) {
        if (code.length <= 6) {
            _otpCode.value = code
            _authState.value = _authState.value.copy(errorMessage = null, successMessage = null)
            Log.d("AuthViewModel", "OTP code updated: $code")
        }
    }

    fun setOtpSentStatus(status: Boolean) {
        _authState.value = _authState.value.copy(isOtpSent = status)
        Log.d("AuthViewModel", "OTP sent status set to: $status")
    }

    fun setErrorMessage(message: String?) {
        _authState.value = _authState.value.copy(errorMessage = message)
        Log.d("AuthViewModel", "Error message set: $message")
    }

    fun sendOtp() {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null,
                isOtpSent = false
            )
            Log.d("AuthViewModel", "Sending OTP for: ${_phoneNumber.value}")

            delay(1000)
            _authState.value = _authState.value.copy(
                isLoading = false,
                isOtpSent = true,
                successMessage = "Mã OTP đã được gửi thành công (test mode)"
            )
            Log.d("AuthViewModel", "OTP sent successfully (test mode) for: ${_phoneNumber.value}")

            /*
            // PHẦN CODE GỐC ĐỂ GỌI API THỰC TẾ
            try {
                val response = authRepository.sendOTP(_phoneNumber.value)
                if (response.success) {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isOtpSent = true,
                        successMessage = response.message
                    )
                    Log.d("AuthViewModel", "OTP sent successfully for: ${_phoneNumber.value}")
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = response.message ?: "Không thể gửi mã OTP",
                        isOtpSent = false
                    )
                    Log.e("AuthViewModel", "Failed to send OTP for: ${_phoneNumber.value}: ${response.message}")
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = "Lỗi kết nối khi gửi OTP: ${e.message}",
                    isOtpSent = false
                )
                Log.e("AuthViewModel", "Exception sending OTP: ${e.message}", e)
            }
            */
        }
    }

    fun verifyOtp() {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )
            Log.d("AuthViewModel", "Verifying OTP: ${_otpCode.value} for phone: ${_phoneNumber.value}")
            try {
                val response = authRepository.verifyOTP(_phoneNumber.value, _otpCode.value)
                if (response.success && response.user != null) {
                    authRepository.saveUserSession(response.user, response.token ?: "")
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        isOtpSent = true,
                        currentUser = response.user,
                        successMessage = response.message
                    )
                    Log.d("AuthViewModel", "OTP verified. User authenticated.")
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = response.message ?: "Mã OTP không chính xác"
                    )
                    Log.e("AuthViewModel", "OTP verification failed for: ${_otpCode.value}: ${response.message}")
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = "Lỗi kết nối khi xác thực OTP: ${e.message}"
                )
                Log.e("AuthViewModel", "Exception verifying OTP: ${e.message}", e)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState()
            _phoneNumber.value = ""
            _otpCode.value = ""
            Log.d("AuthViewModel", "User logged out.")
        }
    }
}

