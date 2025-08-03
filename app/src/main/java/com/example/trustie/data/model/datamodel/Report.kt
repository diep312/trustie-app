package com.example.trustie.data.model.datamodel

import com.google.gson.annotations.SerializedName

data class Report(
    val id: Int,
    val reason: String,
    @SerializedName("report_type")
    val reportType: String, // "phone", "website", "sms", "general"
    val status: String, // "pending", "reviewed", "resolved", "dismissed"
    val priority: String,
    @SerializedName("admin_notes")
    val adminNotes: String? = null,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("reported_phone_id")
    val reportedPhoneId: Int? = null,
    @SerializedName("reported_website_id")
    val reportedWebsiteId: Int? = null,
    @SerializedName("reported_sms_id")
    val reportedSmsId: Int? = null
) 