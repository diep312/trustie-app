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
        if (!isHighRisk) {
            // Don't show overlay for low/medium risk
            return
        }

        if (!hasOverlayPermission()) {
            // If no overlay permission, fall back to a regular high-priority notification
            val callAlertManager = CallAlertNotificationManager(context)
            callAlertManager.showCallAlert(phoneNumber, message, true)
            return
        }

        val title = "🚨 CẢNH BÁO CUỘC GỌI NGUY HIỂM"
        val warningMessage = "$phoneNumber\n$message\n\n" +
                "📢 Ông/Bà hãy thận trọng! Không cung cấp thông tin cá nhân hoặc chuyển tiền."

        val builder = createNotificationBuilder(CHANNEL_ID_HIGH_PRIORITY, title, warningMessage)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setStyle(NotificationCompat.BigTextStyle().bigText(warningMessage))
            .setColor(Color.RED)
            .setVibrate(longArrayOf(0, 1000, 500, 1000, 500, 1000))
            .setLights(Color.RED, 1000, 1000)
            .setOngoing(true)
            .setAutoCancel(false)
            .setCategory(NotificationCompat.CATEGORY_ALARM)

        // Confirm/Receive call action
        val confirmIntent = Intent(context, OverlayNotificationManager::class.java).apply {
            action = "CONFIRM_CALL"
            putExtra("phone_number", phoneNumber)
        }
        val confirmPendingIntent = PendingIntent.getBroadcast(
            context, 4, confirmIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        builder.addAction(R.drawable.ic_phone, "Nhận Cuộc Gọi", confirmPendingIntent)

        // Dismiss action
        val dismissIntent = Intent(context, OverlayNotificationManager::class.java).apply {
            action = "DISMISS_OVERLAY"
            putExtra("notification_id", NOTIFICATION_ID_CALL_OVERLAY)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, 2, dismissIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        builder.addAction(R.drawable.ic_phone_deny, "Bỏ Qua", dismissPendingIntent)

        showNotification(NOTIFICATION_ID_CALL_OVERLAY, builder)
    }

    private fun showUrgentAlert(phoneNumber: String, message: String) {
        val builder = createNotificationBuilder(
            CHANNEL_ID_HIGH_PRIORITY,
            "🚨 NGUY CƠ: Cuộc gọi có nguy cơ lừa đảo",
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