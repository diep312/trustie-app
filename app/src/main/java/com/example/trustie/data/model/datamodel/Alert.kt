package com.example.trustie.data.model.datamodel

import com.google.gson.annotations.SerializedName

data class Alert(
    val id: Int,
    @SerializedName("alert_type")
    val alertType: String, // "scam_detected", "suspicious_activity", "high_risk", "urgent"
    val severity: String, // "low", "medium", "high", "critical"
    val message: String,
    @SerializedName("is_read")
    val isRead: Boolean = false,
    @SerializedName("is_acknowledged")
    val isAcknowledged: Boolean = false,
    @SerializedName("acknowledged_at")
    val acknowledgedAt: String? = null,
    @SerializedName("acknowledged_by")
    val acknowledgedBy: Int? = null,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("family_member_id")
    val familyMemberId: Int? = null,
    @SerializedName("detection_result_id")
    val detectionResultId: Int? = null
) 