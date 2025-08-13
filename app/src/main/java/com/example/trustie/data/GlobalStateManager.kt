package com.example.trustie.data

import com.example.trustie.data.model.datamodel.User
import com.example.trustie.ui.screen.scamresult.ScamResultData
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

    // Change from ImageVerificationResponse? to ScamResultData?
    private val _scamResultData = MutableStateFlow<ScamResultData?>(null)
    val scamResultData: StateFlow<ScamResultData?> = _scamResultData.asStateFlow()

    fun setUser(user: User?) {
        _currentUser.value = user
        _isLoggedIn.value = user != null
    }

    fun clearUser() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    fun getUserId(): Int? = _currentUser.value?.id

    fun getUserName(): String? = _currentUser.value?.name

    fun isUserElderly(): Boolean = _currentUser.value?.isElderly ?: false

    fun setScamResultData(resultData: ScamResultData?) {
        _scamResultData.value = resultData
    }

    fun clearScamResultData() {
        _scamResultData.value = null
    }

    companion object
}
