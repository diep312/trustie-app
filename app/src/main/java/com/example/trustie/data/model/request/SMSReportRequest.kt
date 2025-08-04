package com.example.trustie.data.model.request

import com.google.gson.annotations.SerializedName

data class SMSReportRequest(
    @SerializedName("sender_phone")
    val senderPhone: String,
    val reason: String,
    @SerializedName("user_id")
    val userId: Int,
    val priority: String = "medium",
    @SerializedName("message_body")
    val messageBody: String? = null
) 