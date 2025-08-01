
package com.example.trustie.ui.model

import androidx.compose.ui.graphics.Color

data class RelativeConnection(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val relationship: String, // "Cha", "Mẹ", "Con", "Anh/Chị", "Em", etc.
    val isConnected: Boolean,
    val connectedAt: String?,
    val qrCode: String? = null
) {

    val initials: String
        get() = name.split(" ")
            .filter { it.isNotEmpty() }
            .takeLast(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
            .take(2)


    val avatarColor: Color
        get() {
            val colors = listOf(
                Color(0xFF2196F3), // Blue
                Color(0xFF9C27B0), // Purple
                Color(0xFF4CAF50), // Green
                Color(0xFFFFC107), // Amber
                Color(0xFF00BCD4), // Cyan
                Color(0xFFE91E63)  // Pink
            )
            val hash = name.hashCode()
            return colors[kotlin.math.abs(hash % colors.size)]
        }
}