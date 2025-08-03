package com.example.trustie.data.model.datamodel

import com.google.gson.annotations.SerializedName

data class SMSLog(
    val id: Int,
    @SerializedName("message_body")
    val messageBody: String,
    val sender: String,
    @SerializedName("is_flagged")
    val isFlagged: Boolean = false,
    @SerializedName("flag_reason")
    val flagReason: String? = null,
    @SerializedName("risk_score")
    val riskScore: Int = 0,
    @SerializedName("message_type")
    val messageType: String, // "incoming" or "outgoing"
    val timestamp: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("phone_id")
    val phoneId: Int? = null
) 