package com.example.trustie.data

import com.example.trustie.data.model.datamodel.User
import com.example.trustie.data.model.response.ImageVerificationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalStateManager @Inject constructor() {
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _verificationResponse = MutableStateFlow<ImageVerificationResponse?>(null)
    val verificationResponse: StateFlow<ImageVerificationResponse?> = _verificationResponse.asStateFlow()
    
    fun setUser(user: User?) {
        _currentUser.value = user
        _isLoggedIn.value = user != null
    }
    
    fun clearUser() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }
    
    fun getUserId(): Int? {
        return _currentUser.value?.id
    }
    
    fun getUserName(): String? {
        return _currentUser.value?.name
    }
    
    fun isUserElderly(): Boolean {
        return _currentUser.value?.isElderly ?: false
    }
    
    fun setVerificationResponse(response: ImageVerificationResponse?) {
        _verificationResponse.value = response
    }
    
    fun clearVerificationResponse() {
        _verificationResponse.value = null
    }

    companion object
} 