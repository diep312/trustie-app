package com.example.trustie.data.model.response

import com.google.gson.annotations.SerializedName

data class ReportResponse(
    val id: Int,
    val reason: String,
    @SerializedName("report_type")
    val reportType: String,
    val status: String,
    val priority: String,
    @SerializedName("admin_notes")
    val adminNotes: String? = null,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("resolved_at")
    val resolvedAt: String? = null,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("reported_phone_id")
    val reportedPhoneId: Int? = null,
    @SerializedName("reported_website_id")
    val reportedWebsiteId: Int? = null,
    @SerializedName("reported_sms_id")
    val reportedSmsId: Int? = null
) 