package com.example.trustie.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val _isAlertEnabled = MutableStateFlow(false)
    val isAlertEnabled: StateFlow<Boolean> = _isAlertEnabled.asStateFlow()

    private val _showAlertDialog = MutableStateFlow(false)
    val showAlertDialog: StateFlow<Boolean> = _showAlertDialog.asStateFlow()

    init {
        Log.d("HomeDebug", "HomeViewModel initialized")
    }

    fun toggleAlert() {
        Log.d("HomeDebug", "toggleAlert called")
        _isAlertEnabled.value = !_isAlertEnabled.value
        _showAlertDialog.value = true
        Log.d("HomeDebug", "Alert enabled: ${_isAlertEnabled.value}")
    }


    fun dismissAlertDialog() {
        Log.d("HomeDebug", "dismissAlertDialog called")
        _showAlertDialog.value = false // Ẩn AlertDialog
    }
}
