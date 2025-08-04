package com.example.trustie.data.notification

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.trustie.utils.PermissionHelper

class CallDetectionManager private constructor(private val context: Context) {
    
    companion object {
        private const val TAG = "CallDetectionManager"
        @Volatile
        private var INSTANCE: CallDetectionManager? = null
        
        fun getInstance(context: Context): CallDetectionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CallDetectionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val overlayNotificationManager = OverlayNotificationManager(context)
    private val callAlertNotificationManager = CallAlertNotificationManager(context)
    
    fun startCallDetection() {
        if (!PermissionHelper.hasRequiredPermissions(context)) {
            Log.w(TAG, "Required permissions not granted, cannot start call detection")
            return
        }
        
        Log.d(TAG, "Starting call detection service")
        val serviceIntent = Intent(context, ScamCheckService::class.java)
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start call detection service", e)
        }
    }
    
    fun stopCallDetection() {
        Log.d(TAG, "Stopping call detection service")
        val serviceIntent = Intent(context, ScamCheckService::class.java)
        try {
            context.stopService(serviceIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop call detection service", e)
        }
    }

    fun showCallAlert(phoneNumber: String, message: String, isHighRisk: Boolean) {
        Log.d(TAG, "Showing call alert for $phoneNumber: $message (High risk: $isHighRisk)")
        
        if (PermissionHelper.hasOverlayPermission(context)) {
            overlayNotificationManager.showCallScreenOverlay(phoneNumber, message, isHighRisk)
        } else {
            callAlertNotificationManager.showCallAlert(phoneNumber, message, isHighRisk)
        }
    }
    
    fun dismissAllNotifications() {
        overlayNotificationManager.dismissOverlayNotifications()
        callAlertNotificationManager.dismissCallAlert()
    }
    
    fun isCallDetectionEnabled(): Boolean {
        return PermissionHelper.hasRequiredPermissions(context)
    }
    
    fun hasOverlayPermission(): Boolean {
        return PermissionHelper.hasOverlayPermission(context)
    }
    
    fun requestOverlayPermission() {
        // This should be called from an Activity context
        Log.d(TAG, "Overlay permission should be requested from Activity")
    }
} 