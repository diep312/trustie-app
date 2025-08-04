package com.example.trustie.data.model.datamodel

import com.google.gson.annotations.SerializedName

data class Website(
    val id: Int,
    val domain: String,
    val url: String,
    val description: String? = null,
    @SerializedName("trust_worthy_point")
    val trustWorthyPoint: Double = 0.0,
    @SerializedName("risk_score")
    val riskScore: Int = 0,
    @SerializedName("is_flagged")
    val isFlagged: Boolean = false,
    @SerializedName("flag_reason")
    val flagReason: String? = null,
    @SerializedName("ssl_valid")
    val sslValid: Boolean = false,
    @SerializedName("last_checked")
    val lastChecked: String? = null
) 