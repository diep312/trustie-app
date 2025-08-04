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
import kotlinx.coroutines.withTimeout
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
            android.util.Log.d("AuthViewModel", "checkAuthStatus - starting authentication check")
            try {
                // Add timeout to prevent infinite loading
                withTimeout(5000L) {
                    // First check if we have a current user
                    authRepository.getCurrentUser().collect { user ->
                        android.util.Log.d("AuthViewModel", "checkAuthStatus - user from repository: ${user?.name ?: "null"}")
                        if (user != null) {
                            // User exists, set as authenticated
                            android.util.Log.d("AuthViewModel", "checkAuthStatus - user found, setting as authenticated")
                            globalStateManager.setUser(user)
                            _authState.value = AuthState.Authenticated(user)
                        } else {
                            // No user found, check if logged in flag is set
                            val isLoggedIn = authRepository.isLoggedIn()
                            android.util.Log.d("AuthViewModel", "checkAuthStatus - no user found, isLoggedIn: $isLoggedIn")
                            if (isLoggedIn) {
                                // Inconsistent state - logged in but no user data
                                // Clear the inconsistent state and set as unauthenticated
                                android.util.Log.d("AuthViewModel", "checkAuthStatus - inconsistent state, clearing user data")
                                authRepository.clearUser()
                                _authState.value = AuthState.Unauthenticated
                            } else {
                                android.util.Log.d("AuthViewModel", "checkAuthStatus - setting as unauthenticated")
                                _authState.value = AuthState.Unauthenticated
                            }
                        }
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "checkAuthStatus - error", e)
                // Fallback: if authentication check fails, try to login with fixed user
                android.util.Log.d("AuthViewModel", "checkAuthStatus - falling back to fixed user login")
                try {
                    val user = authRepository.loginWithFixedUser()
                    globalStateManager.setUser(user)
                    _authState.value = AuthState.Authenticated(user)
                } catch (fallbackError: Exception) {
                    android.util.Log.e("AuthViewModel", "checkAuthStatus - fallback also failed", fallbackError)
                    _authState.value = AuthState.Error("Authentication failed: ${e.message}")
                } finally {
                    _isLoading.value = false
                }
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



//    THIS IS FOR TESTING ONLY
    fun loginWithFixedUser() {
        viewModelScope.launch {
            _isLoading.value = true
            android.util.Log.d("AuthViewModel", "loginWithFixedUser - starting login")
            try {
                val user = authRepository.loginWithFixedUser()
                android.util.Log.d("AuthViewModel", "loginWithFixedUser - user created: ${user.name}")
                globalStateManager.setUser(user)
                _authState.value = AuthState.Authenticated(user)
                android.util.Log.d("AuthViewModel", "loginWithFixedUser - login successful")
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "loginWithFixedUser - login failed", e)
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
    
    // Debug method to test DataStore functionality
    fun testDataStore() {
        viewModelScope.launch {
            android.util.Log.d("AuthViewModel", "testDataStore - testing DataStore functionality")
            try {
                val user = authRepository.loginWithFixedUser()
                android.util.Log.d("AuthViewModel", "testDataStore - user saved: ${user.name}")
                
                val isLoggedIn = authRepository.isLoggedIn()
                android.util.Log.d("AuthViewModel", "testDataStore - isLoggedIn: $isLoggedIn")
                
                authRepository.getCurrentUser().collect { retrievedUser ->
                    android.util.Log.d("AuthViewModel", "testDataStore - retrieved user: ${retrievedUser?.name ?: "null"}")
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "testDataStore - error", e)
            }
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

