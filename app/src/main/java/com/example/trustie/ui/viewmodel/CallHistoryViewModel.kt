




package com.example.trustie.ui.viewmodel

import android.util.Log // Import Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.repository.CallHistoryRepository
import com.example.trustie.ui.model.CallHistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CallHistoryViewModel(
    private val repository: CallHistoryRepository = CallHistoryRepository()
) : ViewModel() {

    private val _callHistory = MutableStateFlow<List<CallHistoryItem>>(emptyList())
    val callHistory: StateFlow<List<CallHistoryItem>> = _callHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        Log.d("CallHistoryDebug", "CallHistoryViewModel initialized")
    }

    fun loadCallHistory() {
        viewModelScope.launch {
            Log.d("CallHistoryDebug", "loadCallHistory called")
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = repository.getCallHistory()
                if (response.success) {
                    _callHistory.value = response.data
                    Log.d("CallHistoryDebug", "Call history loaded successfully. Items: ${_callHistory.value.size}")
                } else {
                    _errorMessage.value = response.message ?: "Không thể tải dữ liệu"
                    Log.e("CallHistoryDebug", "Failed to load call history: ${_errorMessage.value}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
                Log.e("CallHistoryDebug", "Exception loading call history: ${e.message}", e)
            } finally {
                _isLoading.value = false
                Log.d("CallHistoryDebug", "loadCallHistory finished. isLoading: $_isLoading.value")
            }
        }
    }

    fun makeCall(phoneNumber: String) {
        Log.d("CallHistoryDebug", "Making call to: $phoneNumber")
    }
}


