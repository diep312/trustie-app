package com.example.trustie.ui.screen.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.model.NotificationItem
import com.example.trustie.repository.alertrepo.AlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val alertRepository: AlertRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Remember last query so refresh can reuse it
    private var lastUnreadOnly = false
    private var lastLimit = 50
    private var lastOffset = 0

    init {
        Log.d("NotificationDebug", "NotificationViewModel initialized")
        loadNotifications()
    }

    fun loadNotifications(
        unreadOnly: Boolean = lastUnreadOnly,
        limit: Int = lastLimit,
        offset: Int = lastOffset
    ) {
        viewModelScope.launch {
            Log.d(
                "NotificationDebug",
                "loadNotifications called. unreadOnly=$unreadOnly, limit=$limit, offset=$offset"
            )
            _isLoading.value = true
            _errorMessage.value = null

            lastUnreadOnly = unreadOnly
            lastLimit = limit
            lastOffset = offset

            val result = alertRepository.getAllNotifications(
                unreadOnly = unreadOnly,
                limit = limit,
                offset = offset
            )

            result
                .onSuccess { list ->
                    _notifications.value = list
                    Log.d("NotificationDebug", "Notifications loaded successfully. Count: ${list.size}")
                }
                .onFailure { e ->
                    _errorMessage.value = e.message ?: "Đã xảy ra lỗi."
                    Log.e("NotificationDebug", "Failed to load notifications", e)
                }

            _isLoading.value = false
        }
    }

    fun refreshNotifications() {
        // Reset offset to 0 for a true refresh
        loadNotifications(lastUnreadOnly, lastLimit, offset = 0)
    }

    // Optional: for the Android system notification use-case
    suspend fun getLatestForSystemNotification(): NotificationItem? {
        return alertRepository.getLatestNotification().getOrNull()
    }
}