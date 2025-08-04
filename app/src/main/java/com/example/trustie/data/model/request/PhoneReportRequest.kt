package com.example.trustie.data.model.request

import com.google.gson.annotations.SerializedName

data class PhoneReportRequest(
    @SerializedName("phone_number")
    val phoneNumber: String,
    val reason: String,
    @SerializedName("user_id")
    val userId: Int,
    val priority: String = "medium"
) 