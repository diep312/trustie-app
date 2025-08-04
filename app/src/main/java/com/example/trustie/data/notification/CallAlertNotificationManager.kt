package com.example.trustie.data.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.core.app.NotificationCompat
import com.example.trustie.R
import com.example.trustie.ui.screen.checkphone.CheckPhoneScreen

class CallAlertNotificationManager(context: Context) : BaseNotificationManager(context) {
    
    companion object {
        private const val NOTIFICATION_ID_CALL_ALERT = 2001
        private const val NOTIFICATION_ID_OVERLAY = 2002
    }


    fun showCallAlert(phoneNumber: String, message: String, isHighRisk: Boolean) {
        val channelId = if (isHighRisk) CHANNEL_ID_HIGH_PRIORITY else CHANNEL_ID_CALL_ALERT
        val priority = if (isHighRisk) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT
        
        val title = if (isHighRisk) "Cảnh báo lừa đảo!" else "Xem cuộc gọi"
        
        val builder = createNotificationBuilder(channelId, title, message)
            .setPriority(priority)
            .setContentTitle(title)
            .setContentText("$phoneNumber\n$message")
            .setStyle(NotificationCompat.BigTextStyle().bigText("$phoneNumber\n$message"))
            .setColor(if (isHighRisk) Color.RED else Color.GREEN)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setLights(Color.RED, 3000, 3000)
            .setAutoCancel(true)
            .setOngoing(false)
        
        // Add action buttons
//        val checkIntent = Intent(context, CheckPhoneScreen()::class.java).apply {
//            putExtra("phone_number", phoneNumber)
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//        }
        
//        val checkPendingIntent = PendingIntent.getActivity(
//            context, 1, checkIntent,
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            } else {
//                PendingIntent.FLAG_UPDATE_CURRENT
//            }
//        )
        
//        builder.addAction(
//            R.drawable.ic_check_number,
//            "Kiểm tra thông tin",
//            checkPendingIntent
//        )
        
        // Add dismiss action
        val dismissIntent = Intent(context, CallAlertNotificationManager::class.java).apply {
            action = "DISMISS_CALL_ALERT"
            putExtra("notification_id", NOTIFICATION_ID_CALL_ALERT)
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
            "Bỏ qua",
            dismissPendingIntent
        )
        
        showNotification(NOTIFICATION_ID_CALL_ALERT, builder)
        
        // If high risk, also show overlay notification
        if (isHighRisk) {
            showOverlayNotification(phoneNumber, message)
        }
    }
    
    private fun showOverlayNotification(phoneNumber: String, message: String) {
        val builder = createNotificationBuilder(
            CHANNEL_ID_HIGH_PRIORITY,
            "Cảnh báo: Cuộc gọi có khả năng lừa đảo",
            "$phoneNumber\n$message"
        ).apply {
            setPriority(NotificationCompat.PRIORITY_MAX)
            setColor(Color.RED)
            setVibrate(longArrayOf(0, 1000, 500, 1000, 500, 1000))
            setLights(Color.RED, 1000, 1000)
            setOngoing(true)
            setAutoCancel(false)
            setCategory(NotificationCompat.CATEGORY_ALARM)
        }
        
        showNotification(NOTIFICATION_ID_OVERLAY, builder)
    }
    
    fun dismissCallAlert() {
        cancelNotification(NOTIFICATION_ID_CALL_ALERT)
        cancelNotification(NOTIFICATION_ID_OVERLAY)
    }
    
    fun dismissOverlayNotification() {
        cancelNotification(NOTIFICATION_ID_OVERLAY)
    }
} 