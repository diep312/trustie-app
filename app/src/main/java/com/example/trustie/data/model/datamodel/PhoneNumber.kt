package com.example.trustie.data.model.datamodel

import com.google.gson.annotations.SerializedName

data class PhoneNumber(
    val id: Int,
    val number: String,
    @SerializedName("country_code")
    val countryCode: String,
    val info: String? = null,
    val origin: String? = null,
    @SerializedName("is_flagged")
    val isFlagged: Boolean = false,
    @SerializedName("flag_reason")
    val flagReason: String? = null,
    @SerializedName("risk_score")
    val riskScore: Int = 0,
    @SerializedName("last_checked")
    val lastChecked: String? = null,
    @SerializedName("owner_id")
    val ownerId: Int? = null
) 