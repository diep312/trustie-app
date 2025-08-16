package com.example.trustie.data.model.enums

enum class AlertType(val value: String) {
    SCAM_DETECTED("scam_detected"),
    SUSPICIOUS_ACTIVITY("suspicious_activity"),
    HIGH_RISK("high_risk"),
    URGENT("urgent"),
    FAMILY_MEMBER_ALERT("family_member_alert"),
    DAILY_REMINDER("daily_reminder"),
    PHONE_RISK("phone_risk"),
    FAMILY_ONLY_ALERT("family_only_alert");

    companion object {
        fun from(raw: String?): AlertType {
            if (raw == null) return DAILY_REMINDER
            return entries.firstOrNull { it.value.equals(raw, ignoreCase = true) } ?: DAILY_REMINDER
        }
    }
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