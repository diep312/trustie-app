package com.example.trustie.data.model

data class QRScanResult(
    val success: Boolean,
    val elderlyUserId: Int? = null,
    val message: String? = null
)

data class LinkFamilyResponse(
    val success: Boolean,
    val message: String? = null,
    val data: Map<String, Any>? = null
)
