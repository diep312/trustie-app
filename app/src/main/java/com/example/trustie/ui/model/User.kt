package com.example.trustie.ui.model

data class User(
    val id: Int,
    val phoneNumber: String,
    val name: String? = null,
    val isVerified: Boolean = false,
    val createdAt: String? = null
)
