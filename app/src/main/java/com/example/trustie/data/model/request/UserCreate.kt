package com.example.trustie.data.model.request

import com.google.gson.annotations.SerializedName

data class UserCreate(
    val name: String,
    val email: String? = null,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("is_elderly")
    val isElderly: Boolean = false
) 