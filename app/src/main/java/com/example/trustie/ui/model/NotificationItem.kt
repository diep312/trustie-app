package com.example.trustie.ui.model

data class NotificationItem(
    val id: String,
    val title: String,
    val phoneNumber: String,
    val time: String,
    val location: String = "Vietnam",
    val isRead: Boolean = false
)

data class NotificationResponse(
    val success: Boolean,
    val data: List<NotificationItem>,
    val message: String? = null
)