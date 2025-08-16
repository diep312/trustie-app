package com.example.trustie.data.model


data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val phoneNumber: String?,
    val time: String,           // formatted from createdAt
    val location: String? = "Vietnam",
    val isRead: Boolean = false
)