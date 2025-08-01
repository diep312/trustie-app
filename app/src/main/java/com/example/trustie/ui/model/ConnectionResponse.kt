package com.example.trustie.ui.model

data class ConnectionResponse(
    val success: Boolean,
    val qrCode: String? = null,
    val connections: List<RelativeConnection> = emptyList(),
    val message: String? = null
)
