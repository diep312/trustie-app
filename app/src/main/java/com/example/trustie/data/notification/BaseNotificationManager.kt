package com.example.trustie.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.trustie.MainActivity
import com.example.trustie.R

abstract class BaseNotificationManager(protected val context: Context) {
    
    companion object {
        const val CHANNEL_ID_GENERAL = "general_notifications"
        const val CHANNEL_ID_CALL_ALERT = "call_alert_notifications"
        const val CHANNEL_ID_HIGH_PRIORITY = "high_priority_notifications"
    }
    
    protected val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    
    init {
        createNotificationChannels()
    }
    
    protected fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_ID_GENERAL,
                    "General Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Thông báo phổ biến cho ứng dụng"
                },
                NotificationChannel(
                    CHANNEL_ID_CALL_ALERT,
                    "Call Alert Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Thông báo có mức ưu tiên cao hơn"
                    enableVibration(true)
                    enableLights(true)
                },
                NotificationChannel(
                    CHANNEL_ID_HIGH_PRIORITY,
                    "High Priority Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Thông báo có mức độ ưu tiên cao nhất"
                    enableVibration(true)
                    enableLights(true)
                }
            )
            
            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    protected fun createPendingIntent(
        targetActivity: Class<*> = MainActivity::class.java,
        extras: Map<String, String> = emptyMap()
    ): PendingIntent {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val intent = Intent(context, targetActivity).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            extras.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        return PendingIntent.getActivity(context, 0, intent, flags)
    }

    protected fun createNotificationBuilder(
        channelId: String,
        title: String,
        content: String,
        icon: Int = R.drawable.ic_shield
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(icon)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }
    
    protected fun showNotification(
        notificationId: Int,
        builder: NotificationCompat.Builder
    ) {
        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    protected fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
    
    protected fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
} 