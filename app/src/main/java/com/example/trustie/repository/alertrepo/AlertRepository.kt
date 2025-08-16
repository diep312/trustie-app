package com.example.trustie.repository.alertrepo

import com.example.trustie.data.model.NotificationItem

interface AlertRepository {
    suspend fun getAllNotifications(
        unreadOnly: Boolean = false,
        limit: Int = 50,
        offset: Int = 0
    ): Result<List<NotificationItem>>

    suspend fun getLatestNotification(): Result<NotificationItem?>
}