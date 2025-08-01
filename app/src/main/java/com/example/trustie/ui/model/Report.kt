package com.example.trustie.ui.model

// Model cho yêu cầu báo cáo số điện thoại
data class ReportRequest(
    val phoneNumber: String,
    val reason: String,
    val description: String? = null
)

// Model cho phản hồi từ API sau khi báo cáo
data class ReportResponse(
    val success: Boolean,
    val message: String? = null
)
