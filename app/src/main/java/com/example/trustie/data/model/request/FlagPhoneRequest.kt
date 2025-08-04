package com.example.trustie.data.model.request

import com.google.gson.annotations.SerializedName

data class FlagPhoneRequest(
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("flag_reason")
    val flagReason: String,
    @SerializedName("risk_score")
    val riskScore: Int = 50
) 