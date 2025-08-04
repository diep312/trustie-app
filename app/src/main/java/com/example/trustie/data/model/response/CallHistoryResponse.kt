package com.example.trustie.data.model.response
import com.example.trustie.data.model.datamodel.CallHistoryItem


data class CallHistoryResponse(
    val success: Boolean,
    val data: List<CallHistoryItem>,
    val message: String? = null
)