package com.example.trustie.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.example.trustie.data.GlobalStateManager
import com.example.trustie.di.GlobalStateEntryPoint
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

class CallDetectionReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "CallDetectionReceiver"
        private var lastState = TelephonyManager.CALL_STATE_IDLE
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            TelephonyManager.ACTION_PHONE_STATE_CHANGED -> {
                handlePhoneStateChanged(context, intent)
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d(TAG, "Boot completed, starting call detection service")
                startScamCheckService(context)
            }
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                Log.d(TAG, "Package replaced, starting call detection service")
                startScamCheckService(context)
            }
        }


    }
    
    private fun handlePhoneStateChanged(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        
        Log.d(TAG, "Phone state changed: $state, Number: $phoneNumber")
        
        when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    lastState = TelephonyManager.CALL_STATE_RINGING
                    phoneNumber?.let { number ->
                        Log.d(TAG, "Incoming call detected: $number")
                        checkPhoneNumberForScam(context, number)
                    }
                }
            }
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                lastState = TelephonyManager.CALL_STATE_OFFHOOK
            }
            TelephonyManager.EXTRA_STATE_IDLE -> {
                lastState = TelephonyManager.CALL_STATE_IDLE
            }
        }
    }
    
    private fun checkPhoneNumberForScam(context: Context, phoneNumber: String) {
        val globalStateManager = EntryPointAccessors.fromApplication(
            context.applicationContext,
            GlobalStateEntryPoint::class.java
        ).globalStateManager()

        val userId = globalStateManager.getUserId()
        if (userId == null) {
            Log.d(TAG, "User not logged in, skipping scam check")
            return
        }
        
        // Start the scam check service
        val serviceIntent = Intent(context, ScamCheckService::class.java).apply {
            putExtra(ScamCheckService.EXTRA_PHONE_NUMBER, phoneNumber)
            putExtra(ScamCheckService.EXTRA_USER_ID, userId)
        }
        
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            Log.d(TAG, "Started scam check service for number: $phoneNumber")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start scam check service", e)
        }
    }
    
    private fun startScamCheckService(context: Context) {
        val serviceIntent = Intent(context, ScamCheckService::class.java)
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            Log.d(TAG, "Started scam check service on boot")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start scam check service on boot", e)
        }
    }
}