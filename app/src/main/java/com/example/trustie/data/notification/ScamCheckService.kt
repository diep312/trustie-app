package com.example.trustie.data.notification

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.trustie.MainActivity
import com.example.trustie.R
import com.example.trustie.data.GlobalStateManager
import com.example.trustie.data.model.request.PhoneCheckRequest
import com.example.trustie.data.model.response.PhoneCheckResponse
import com.example.trustie.repository.phonerepo.PhoneRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class ScamCheckService : Service() {
    
    companion object {
        private const val TAG = "ScamCheckService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "scam_check_channel"
        
        const val EXTRA_PHONE_NUMBER = "phone_number"
        const val EXTRA_USER_ID = "user_id"
    }
    
    @Inject
    lateinit var phoneRepository: PhoneRepository
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d(TAG, "ScamCheckService created")
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "ScamCheckService started")
        
        // Start foreground service only on API 26+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, createNotification("Checking incoming call..."))
        }
        
        // Handle the phone number check
        intent?.let { handlePhoneCheck(it) }
        
        return START_NOT_STICKY
    }
    
    private fun handlePhoneCheck(intent: Intent) {
        val phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER)
        val userId = intent.getIntExtra(EXTRA_USER_ID, -1)
        
        if (phoneNumber == null || userId == -1) {
            Log.e(TAG, "Invalid phone number or user ID")
            stopSelf()
            return
        }
        
        Log.d(TAG, "Checking phone number: $phoneNumber for user: $userId")
        
        serviceScope.launch {
            try {
                val request = PhoneCheckRequest(
                    phoneNumber = phoneNumber,
                    userId = userId
                )
                
                val response = phoneRepository.checkPhoneNumber(phoneNumber, userId)
                
                withContext(Dispatchers.Main) {
                    handlePhoneCheckResult(response, phoneNumber)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error checking phone number", e)
                withContext(Dispatchers.Main) {
                    // Show generic notification on error
                    showCallAlertNotification(phoneNumber, "Unable to verify number", false)
                }
            } finally {
                stopSelf()
            }
        }
    }
    
    private fun handlePhoneCheckResult(response: PhoneCheckResponse, phoneNumber: String) {
        val isFlagged = response.isFlagged ?: false
        val riskLevel = response.riskScore ?: 0
        val description = response.info ?: "Không có thông tin chi tiết..."
        
        Log.d(TAG, "Phone check result - Flagged: $isFlagged, Risk: $riskLevel")
        
        if (isFlagged) {
            showCallAlertNotification(phoneNumber, "⚠️ Đã nhận diện cuộc gọi có khả năng lừa đảo! Mức độ nguy hại: $riskLevel", true)
        } else {
            showCallAlertNotification(phoneNumber, "✅ Số điện thoại an toàn", false)
        }
    }
    
    private fun showCallAlertNotification(phoneNumber: String, message: String, isHighRisk: Boolean) {
        val overlayManager = OverlayNotificationManager(this)
        overlayManager.showCallScreenOverlay(phoneNumber, message, isHighRisk)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Scam Check Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background service for checking incoming calls"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(content: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Trustie Call Protection")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_shield)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d(TAG, "ScamCheckService destroyed")
    }
} 