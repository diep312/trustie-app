package com.example.trustie.ui.model

class RelativeConnection(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val relationship: String, // "Cha", "Mẹ", "Con", "Anh/Chị", "Em", etc.
    val isConnected: Boolean,
    val connectedAt: String?,
    val qrCode: String? = null
)

data class ConnectionResponse(
    val success: Boolean,
    val qrCode: String? = null,
    val connections: List<RelativeConnection> = emptyList(),
    val message: String? = null
)