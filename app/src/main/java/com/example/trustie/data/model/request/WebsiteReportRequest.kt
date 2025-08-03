package com.example.trustie.data.model.request

import com.google.gson.annotations.SerializedName

data class WebsiteReportRequest(
    val domain: String,
    val reason: String,
    @SerializedName("user_id")
    val userId: Int,
    val priority: String = "medium",
    val url: String? = null
) 