package com.example.trustie.data.model.datamodel

data class CallHistoryItem(
    val id: String,
    val contactName: String,
    val phoneNumber: String,
    val time: String,
    val country: String = "Vietnam",
    val callType: String = "Cuộc gọi đến",
    val isSuspicious: Boolean = false
)