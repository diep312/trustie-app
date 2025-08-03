package com.example.trustie.data.model.enums

enum class AlertType(val value: String) {
    SCAM_DETECTED("scam_detected"),
    SUSPICIOUS_ACTIVITY("suspicious_activity"),
    HIGH_RISK("high_risk"),
    URGENT("urgent")
}

enum class Severity(val value: String) {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),
    CRITICAL("critical")
}

enum class ReportPriority(val value: String) {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),
    URGENT("urgent")
} 