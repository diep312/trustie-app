package com.example.trustie.ui.screen.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.model.NotificationItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        Log.d("NotificationDebug", "NotificationViewModel initialized")
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            Log.d("NotificationDebug", "loadNotifications called")
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Mô phỏng API call
                delay(1000)

                val mockNotifications = listOf(
                    NotificationItem(
                        id = "1",
                        title = "Cảnh báo lừa đảo từ điện thoại người thân",
                        phoneNumber = "09 123 456 78",
                        time = "18:12",
                        location = "Vietnam"
                    ),
                    NotificationItem(
                        id = "2",
                        title = "Cảnh báo lừa đảo từ điện thoại người thân",
                        phoneNumber = "09 123 456 78",
                        time = "18:12",
                        location = "Vietnam"
                    )
                )

                _notifications.value = mockNotifications
                Log.d("NotificationDebug", "Notifications loaded successfully. Count: ${mockNotifications.size}")
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
                Log.e("NotificationDebug", "Exception loading notifications: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshNotifications() {
        loadNotifications()
    }
}