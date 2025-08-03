package com.example.trustie.data.model.response

import com.google.gson.annotations.SerializedName

data class PhoneNumber(
    val number: String,
    @SerializedName("country_code")
    val countryCode: String? = null,
    val info: String? = null,
    val origin: String? = null,
    val id: Int,
    @SerializedName("is_flagged")
    val isFlagged: Boolean,
    @SerializedName("flag_reason")
    val flagReason: String? = null,
    @SerializedName("risk_score")
    val riskScore: Int,
    @SerializedName("last_checked")
    val lastChecked: String? = null,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("owner_id")
    val ownerId: Int? = null
) 