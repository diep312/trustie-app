package com.example.trustie.data.model.response

import com.google.gson.annotations.SerializedName

data class PhoneCheckResponse(
    val found: Boolean,
    @SerializedName("is_flagged")
    val isFlagged: Boolean,
    @SerializedName("flag_reason")
    val flagReason: String? = null,
    @SerializedName("risk_score")
    val riskScore: Int,
    val info: String? = null,
    val origin: String? = null,
    @SerializedName("last_checked")
    val lastChecked: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    val message: String? = null
) 