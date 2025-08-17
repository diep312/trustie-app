package com.example.trustie.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.GlobalStateManager
import com.example.trustie.data.model.datamodel.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val globalStateManager: GlobalStateManager
) : ViewModel() {

    private val _isAlertEnabled = MutableStateFlow(true)
    val isAlertEnabled: StateFlow<Boolean> = _isAlertEnabled.asStateFlow()

    private val _showAlertDialog = MutableStateFlow(false)
    val showAlertDialog: StateFlow<Boolean> = _showAlertDialog.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        Log.d("HomeDebug", "HomeViewModel initialized")
        loadUserInfo()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            globalStateManager.currentUser.collect { user ->
                _currentUser.value = user
                Log.d("HomeDebug", "User loaded: ${user?.name}")
            }
        }
    }

    fun toggleAlert() {
        Log.d("HomeDebug", "toggleAlert called")
        _isAlertEnabled.value = !_isAlertEnabled.value
        _showAlertDialog.value = true
        Log.d("HomeDebug", "Alert enabled: ${_isAlertEnabled.value}")
    }

    fun dismissAlertDialog() {
        Log.d("HomeDebug", "dismissAlertDialog called")
        _showAlertDialog.value = false
    }

    fun getUserName(): String {
        return _currentUser.value?.name ?: "User"
    }

    fun isUserElderly(): Boolean {
        return _currentUser.value?.isElderly ?: false
    }

    fun getUserId(): Int? {
        return _currentUser.value?.id
    }
}