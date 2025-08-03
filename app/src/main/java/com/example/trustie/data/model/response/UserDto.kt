package com.example.trustie.data.model

import com.example.trustie.data.model.datamodel.User


data class UserResponse(
    val id: Int,
    val name: String?,
    val email: String?,
    val device_id: String?,
    val is_elderly: Boolean,
    val is_active: Boolean,
    val created_at: String,
    val updated_at: String
)


fun UserResponse.toUser(): User {
    return User(
        id = id,
        name = name ?: "Người dùng",
        phoneNumber = "",
        isVerified = true,
        createdAt = created_at
    )
}
