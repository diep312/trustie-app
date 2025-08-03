package com.example.trustie.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.GlobalStateManager
import com.example.trustie.data.model.datamodel.User
import com.example.trustie.repository.authrepo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val globalStateManager: GlobalStateManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _isOtpSent = MutableStateFlow(false)
    val isOtpSent: StateFlow<Boolean> = _isOtpSent.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val isLoggedIn = authRepository.isLoggedIn()
                if (isLoggedIn) {
                    // User is already logged in, get current user
                    authRepository.getCurrentUser().collect { user ->
                        if (user != null) {
                            globalStateManager.setUser(user)
                            _authState.value = AuthState.Authenticated(user)
                        } else {
                            _authState.value = AuthState.Unauthenticated
                        }
                    }
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error checking auth status: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setOtpCode(code: String) {
        _otpCode.value = code
    }

    fun setPhoneNumber(number: String) {
        _phoneNumber.value = number
    }

    fun sendOtp() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Simulate OTP sending
                _isOtpSent.value = true
                _authState.value = AuthState.Success("OTP sent successfully")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Failed to send OTP: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun verifyOtp() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Simulate OTP verification
                val user = authRepository.loginWithFixedUser()
                globalStateManager.setUser(user)
                _authState.value = AuthState.Authenticated(user)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("OTP verification failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginWithFixedUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = authRepository.loginWithFixedUser()
                globalStateManager.setUser(user)
                _authState.value = AuthState.Authenticated(user)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Login failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                authRepository.logout()
                globalStateManager.clearUser()
                _authState.value = AuthState.Unauthenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Logout failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
    data class Success(val message: String) : AuthState()
    
    val isLoading: Boolean
        get() = this is Initial
    
    val isAuthenticated: Boolean
        get() = this is Authenticated
    
    val errorMessage: String?
        get() = if (this is Error) message else null
    
    val successMessage: String?
        get() = if (this is Success) message else null
}

