package com.example.trustie.data.model.request

import com.google.gson.annotations.SerializedName

data class LinkRequest(
    @SerializedName("scanned_payload")
    val scannedPayload: String,
    @SerializedName("family_user_id")
    val familyUserId: Int,
    val name: String,
    val relationship: String? = null,
    @SerializedName("phone_number")
    val phoneNumber: String? = null,
    val email: String? = null
) 