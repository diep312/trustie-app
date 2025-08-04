package com.example.trustie.ui.screen.checkphone

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.GlobalStateManager
import com.example.trustie.data.model.PhoneCheckItem
import com.example.trustie.data.model.response.PhoneCheckResponse
import com.example.trustie.repository.phonerepo.PhoneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckPhoneViewModel @Inject constructor(
    private val repository: PhoneRepository,
    private val globalStateManager: GlobalStateManager
) : ViewModel() {

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _checkResult = MutableStateFlow<PhoneCheckItem?>(null)
    val checkResult: StateFlow<PhoneCheckItem?> = _checkResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        Log.d("CheckPhoneDebug", "CheckPhoneViewModel initialized")
    }

    fun updatePhoneNumber(number: String) {
        _phoneNumber.value = number
        // Clear previous results when phone number changes
        if (_checkResult.value != null) {
            _checkResult.value = null
            _errorMessage.value = null
        }
    }

    fun checkPhoneNumber() {
        val number = _phoneNumber.value.trim()
        if (number.isEmpty()) {
            _errorMessage.value = "Vui lòng nhập số điện thoại"
            return
        }

        if (!isValidPhoneNumber(number)) {
            _errorMessage.value = "Số điện thoại không hợp lệ"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Get user ID from global state
                val userId = globalStateManager.getUserId()
                Log.d("CheckPhoneDebug", "Checking phone number: $number with userId: $userId")
                
                val response = repository.checkPhoneNumber(number, userId)

                // Convert PhoneCheckResponse to PhoneCheckItem
                val phoneCheckItem = PhoneCheckItem(
                    phoneNumber = number,
                    isSuspicious = response.isFlagged,
                    riskLevel = if (response.isFlagged) "High" else "Low",
                    reportCount = 0,
                    lastReported = null,
                    description = response.flagReason
                )
                
                _checkResult.value = phoneCheckItem
                Log.d("CheckPhoneDebug", "Phone check successful: $phoneCheckItem")
                
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
                Log.e("CheckPhoneDebug", "Exception during phone check: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearResults() {
        _checkResult.value = null
        _errorMessage.value = null
    }

    private fun isValidPhoneNumber(number: String): Boolean {
        val cleanNumber = number.replace("\\s".toRegex(), "")
        return cleanNumber.matches(Regex("^(\\+84|84|0)[3-9][0-9]{8}$"))
    }

    fun startVoiceInput() {
        Log.d("CheckPhoneDebug", "Voice input requested")
        _errorMessage.value = "Tính năng nhập bằng giọng nói đang được phát triển"
    }
}