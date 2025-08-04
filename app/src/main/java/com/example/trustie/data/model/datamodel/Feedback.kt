package com.example.trustie.data.model.datamodel

import com.google.gson.annotations.SerializedName

data class Feedback(
    val id: Int,
    @SerializedName("feedback_type")
    val feedbackType: String, // "bug_report", "feature_request", "general", "accuracy"
    val info: String,
    val rating: Int, // 1-5
    val status: String, // "open", "in_progress", "resolved", "closed"
    @SerializedName("admin_response")
    val adminResponse: String? = null,
    @SerializedName("user_id")
    val userId: Int
) 