package com.example.trustie.data.model.response

import com.google.gson.annotations.SerializedName

data class AlertResponse(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("alert_type")
    val alertType: String,
    val severity: String,
    val message: String,
    @SerializedName("is_read")
    val isRead: Boolean,
    @SerializedName("is_acknowledged")
    val isAcknowledged: Boolean,
    @SerializedName("created_at")
    val createdAt: String
) 