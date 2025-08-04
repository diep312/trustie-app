package com.example.trustie.data.model.datamodel

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val name: String,
    val email: String?,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("is_elderly")
    val isElderly: Boolean,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)