package com.example.trustie.repository.alertrepo

import android.content.Context
import com.example.trustie.data.GlobalStateManager
import com.example.trustie.data.api.ApiManager
import com.example.trustie.data.model.NotificationItem
import com.example.trustie.data.model.response.AlertResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AlertRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val globalStateManager: GlobalStateManager
) : AlertRepository {

    val api = ApiManager.alertApi
    override suspend fun getAllNotifications(
        unreadOnly: Boolean,
        limit: Int,
        offset: Int
    ): Result<List<NotificationItem>> = runCatching {
        val userId = globalStateManager.getUserId()
            ?: error("User not logged in")

        val remote = api.getUserAlerts(
            userId = userId,
            limit = limit,
            offset = offset,
            unreadOnly = unreadOnly
        )

        remote
            .sortedByDescending { it.createdAtInstant() } // server should sort, we also guard here
            .map { it.toNotificationItem() }
    }

    override suspend fun getLatestNotification(): Result<NotificationItem?> = runCatching {
        val userId = globalStateManager.getUserId()
            ?: error("User not logged in")

        val remote = api.getUserAlerts(
            userId = userId,
            limit = 1,
            offset = 0,
            unreadOnly = false
        )

        // If API doesn’t guarantee sorting, pick the max by createdAt
        remote.maxByOrNull { it.createdAtInstant() }?.toNotificationItem()
    }
}

/* ---------- Mappers & helpers ---------- */

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

private fun AlertResponse.toNotificationItem(): NotificationItem {
    val displayTime = createdAtInstant()
        .atZone(ZoneId.systemDefault())
        .format(timeFormatter)

    return NotificationItem(
        id = id.toString(),
        title = titleFromType(alertType, null),
        time = displayTime,
        message = message,
        type = alertType,
        phoneNumber = null,
        location = null,
        isRead = false,
    )
}

/**
 * Pick a human-friendly Vietnamese title based on alert type.
 * Adjust wording as you wish.
 */
private fun titleFromType(type: String?, familyMemberName: String?): String {
    return when (type?.lowercase()) {
        "scam_detected" -> "Phát hiện số điện thoại khả nghi"
        "suspicious_activity" -> "Hoạt động đáng ngờ"
        "high_risk" -> "Cảnh báo rủi ro cao"
        "urgent" -> "Khẩn cấp"
        "family_member_alert" ->
            if (!familyMemberName.isNullOrBlank())
                "Người thân của bạn vừa nhận được cuộc gọi khả nghi!"
            else
                "Cảnh báo cho người thân"
        "daily_reminder" -> "Nhắc nhở"
        "phone_risk" -> "Cảnh báo rủi ro số điện thoại"
        "family_only_alert" -> "Cảnh báo cho gia đình"
        else -> "Thông báo"
    }
}

/**
 * Robust parsing for created_at (ISO-8601 preferred).
 * Fallbacks to acknowledged_at, then a stable pseudo-order using id.
 * Make sure coreLibraryDesugaring is enabled if you target <26.
 */
private fun AlertResponse.createdAtInstant(): Instant {
    fun parse(s: String?): Instant? {
        if (s.isNullOrBlank()) return null
        return try {
            Instant.parse(s)
        } catch (_: Throwable) {
            try {
                OffsetDateTime.parse(s).toInstant()
            } catch (_: Throwable) {
                null
            }
        }
    }

    return parse(createdAt)
        ?: Instant.now().minusSeconds(id.toLong())
}