package com.example.trustie.data.model.datamodel

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val name: String,
    val email: String? = null,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("is_elderly")
    val isElderly: Boolean = false,
    @SerializedName("is_active")
    val isActive: Boolean = true,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)