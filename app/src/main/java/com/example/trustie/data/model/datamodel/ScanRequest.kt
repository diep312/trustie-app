package com.example.trustie.data.model.datamodel

import com.google.gson.annotations.SerializedName

data class ScanRequest(
    val id: Int,
    @SerializedName("source_from")
    val sourceFrom: String, // "manual", "automatic", "scheduled"
    @SerializedName("source_type")
    val sourceType: String, // "phone", "screenshot", "website", "sms"
    val status: String, // "pending", "processing", "completed", "failed"
    val priority: String, // "low", "medium", "high", "urgent"
    val notes: String? = null,
    val timestamp: String,
    @SerializedName("completed_at")
    val completedAt: String? = null,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("screenshot_id")
    val screenshotId: Int? = null,
    @SerializedName("phone_id")
    val phoneId: Int? = null,
    @SerializedName("website_id")
    val websiteId: Int? = null,
    @SerializedName("sms_id")
    val smsId: Int? = null
) 