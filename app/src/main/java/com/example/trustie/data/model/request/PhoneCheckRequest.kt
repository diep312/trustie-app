package com.example.trustie.data.model.request

import com.google.gson.annotations.SerializedName

data class PhoneCheckRequest(
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("user_id")
    val userId: Int? = null
) 