package com.example.trustie.data.model.response

import com.google.gson.annotations.SerializedName

data class User(
    val name: String,
    val email: String? = null,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("is_elderly")
    val isElderly: Boolean = false,
    val id: Int,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
) 