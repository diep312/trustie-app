package com.example.trustie.ui.model

data class CallHistoryItem(
    val id: String,
    val contactName: String,
    val phoneNumber: String,
    val time: String,
    val country: String = "Vietnam",
    val callType: String = "Cuộc gọi đến",
    val isSuspicious: Boolean = false
)

data class CallHistoryResponse(
    val success: Boolean,
    val data: List<CallHistoryItem>,
    val message: String? = null
)