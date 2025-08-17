package com.example.trustie.data.notification

enum class RiskLevel {
    SAFE,
    MEDIUM,
    HIGH;

    fun getColor(): Int {
        return when (this) {
            SAFE -> android.graphics.Color.GREEN
            MEDIUM -> android.graphics.Color.YELLOW
            HIGH -> android.graphics.Color.RED
        }
    }

    fun getDisplayName(): String {
        return when (this) {
            SAFE -> "An toàn"
            MEDIUM -> "Cần thận trọng"
            HIGH -> "Nguy hiểm"
        }
    }

    fun getWarningMessage(): String {
        return when (this) {
            SAFE -> "Cuộc gọi có vẻ an toàn"
            MEDIUM -> "Hãy thận trọng với cuộc gọi này"
            HIGH -> "🚨 CẢNH BÁO: Cuộc gọi có khả năng lừa đảo cao!"
        }
    }
}