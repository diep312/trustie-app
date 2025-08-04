package com.example.trustie.data.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.core.app.NotificationCompat
import com.example.trustie.R
import com.example.trustie.ui.screen.checkphone.CheckPhoneScreen

class OverlayNotificationManager(context: Context) : BaseNotificationManager(context) {

    companion object {
        private const val NOTIFICATION_ID_CALL_OVERLAY = 3001
        private const val NOTIFICATION_ID_URGENT_ALERT = 3002
    }


    fun showCallScreenOverlay(phoneNumber: String, message: String, isHighRisk: Boolean) {
        if (!hasOverlayPermission()) {
            // Fallback to regular notification if overlay permission not granted
            val callAlertManager = CallAlertNotificationManager(context)
            callAlertManager.showCallAlert(phoneNumber, message, isHighRisk)
            return
        }

        val title = if (isHighRisk) "Cảnh báo lừa đảo!" else "Xem thông tin cuộc gọi"
        val channelId = if (isHighRisk) CHANNEL_ID_HIGH_PRIORITY else CHANNEL_ID_CALL_ALERT

        val builder = createNotificationBuilder(channelId, title, message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentTitle(title)
            .setContentText("$phoneNumber\n$message")
            .setStyle(NotificationCompat.BigTextStyle().bigText("$phoneNumber\n$message"))
            .setColor(if (isHighRisk) Color.RED else Color.GREEN)
            .setVibrate(longArrayOf(0, 1000, 500, 1000, 500, 1000))
            .setLights(if (isHighRisk) Color.RED else Color.GREEN, 1000, 1000)
            .setOngoing(true)
            .setAutoCancel(false)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
//            .setFullScreenIntent(createFullScreenPendingIntent(phoneNumber), true)

//        // Add action buttons
//        val checkIntent = Intent(context, CheckPhoneScreen()::class.java).apply {
//            putExtra("phone_number", phoneNumber)
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//        }
//
//        val checkPendingIntent = PendingIntent.getActivity(
//            context, 1, checkIntent,
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            } else {
//                PendingIntent.FLAG_UPDATE_CURRENT
//            }
//        )
//
//        builder.addAction(
//            R.drawable.ic_check_number,
//            "Check Details",
//            checkPendingIntent
//        )

        // Add block action for high risk calls
        if (isHighRisk) {
            val blockIntent = Intent(context, OverlayNotificationManager::class.java).apply {
                action = "BLOCK_CALL"
                putExtra("phone_number", phoneNumber)
            }

            val blockPendingIntent = PendingIntent.getBroadcast(
                context, 3, blockIntent,
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )

            builder.addAction(
                R.drawable.ic_shield,
                "Block Call",
                blockPendingIntent
            )
        }

        // Add dismiss action
        val dismissIntent = Intent(context, OverlayNotificationManager::class.java).apply {
            action = "DISMISS_OVERLAY"
            putExtra("notification_id", NOTIFICATION_ID_CALL_OVERLAY)
        }

        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, 2, dismissIntent,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        builder.addAction(
            R.drawable.ic_arrow_left,
            "Dismiss",
            dismissPendingIntent
        )

        showNotification(NOTIFICATION_ID_CALL_OVERLAY, builder)

        // Show additional urgent alert for high risk calls
        if (isHighRisk) {
            showUrgentAlert(phoneNumber, message)
        }
    }

    private fun showUrgentAlert(phoneNumber: String, message: String) {
        val builder = createNotificationBuilder(
            CHANNEL_ID_HIGH_PRIORITY,
            "🚨 URGENT: Potential Scam Call",
            "$phoneNumber\n$message"
        ).apply {
            setPriority(NotificationCompat.PRIORITY_MAX)
            setColor(Color.RED)
            setVibrate(longArrayOf(0, 2000, 1000, 2000, 1000, 2000))
            setLights(Color.RED, 2000, 2000)
            setOngoing(true)
            setAutoCancel(false)
            setCategory(NotificationCompat.CATEGORY_ALARM)
//            setFullScreenIntent(createFullScreenPendingIntent(phoneNumber), true)
        }

        showNotification(NOTIFICATION_ID_URGENT_ALERT, builder)
    }

//    @Composable
//    private fun createFullScreenPendingIntent(phoneNumber: String): PendingIntent {
//        val intent = Intent(context, CheckPhoneScreen()::class.java).apply {
//            putExtra("phone_number", phoneNumber)
//            putExtra("from_overlay", true)
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//        }
//
//        return PendingIntent.getActivity(
//            context, 0, intent,
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            } else {
//                PendingIntent.FLAG_UPDATE_CURRENT
//            }
//        )
//    }

    private fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.provider.Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    fun dismissOverlayNotifications() {
        cancelNotification(NOTIFICATION_ID_CALL_OVERLAY)
        cancelNotification(NOTIFICATION_ID_URGENT_ALERT)
    }

    fun dismissUrgentAlert() {
        cancelNotification(NOTIFICATION_ID_URGENT_ALERT)
    }
}