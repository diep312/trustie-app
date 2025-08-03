package com.example.trustie.data.model

data class ConnectionResponse(
    val success: Boolean,
    val qrCode: String? = null,
    val connections: List<RelativeConnection> = emptyList(),
    val message: String? = null
)
