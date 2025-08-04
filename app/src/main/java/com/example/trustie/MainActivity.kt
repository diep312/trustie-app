package com.example.trustie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.trustie.data.notification.CallDetectionManager
import com.example.trustie.navigation.AppNavigation
import com.example.trustie.ui.base.BaseAuthenticatedActivity
import com.example.trustie.ui.screen.auth.AuthViewModel
import com.example.trustie.utils.PermissionHelper

class MainActivity : BaseAuthenticatedActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    private var navController: NavHostController? = null
    private lateinit var callDetectionManager: CallDetectionManager
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            Log.d(TAG, "All permissions granted, starting call detection")
            callDetectionManager.startCallDetection()
        } else {
            Log.w(TAG, "Some permissions were denied")
        }
    }
    
    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (PermissionHelper.hasOverlayPermission(this)) {
            Log.d(TAG, "Overlay permission granted")
        } else {
            Log.w(TAG, "Overlay permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callDetectionManager = CallDetectionManager.getInstance(this)
        handleIntent(intent)
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.scheme == "trustie" && uri.host == "call_alert") {
                val phoneNumber = uri.getQueryParameter("phone")
                val message = uri.getQueryParameter("message")
                val isHighRisk = uri.getQueryParameter("high_risk")?.toBoolean() ?: false
                
                if (phoneNumber != null && message != null) {
                    callDetectionManager.showCallAlert(phoneNumber, message, isHighRisk)
                }
            }
        }
    }
    
    @Composable
    override fun MainContent(authViewModel: AuthViewModel) {
        navController = rememberNavController()
        
        // Initialize call detection when user is authenticated
        LaunchedEffect(Unit) {
            initializeCallDetection()
        }
        
        AppNavigation(
            navController = navController!!,
            authViewModel = authViewModel
        )
    }
    
    private fun initializeCallDetection() {
        if (PermissionHelper.hasRequiredPermissions(this)) {
            Log.d(TAG, "Required permissions already granted, starting call detection")
            callDetectionManager.startCallDetection()
        } else {
            Log.d(TAG, "Requesting required permissions")
            permissionLauncher.launch(PermissionHelper.REQUIRED_PERMISSIONS)
        }
        
        // Request overlay permission if not granted
        if (!PermissionHelper.hasOverlayPermission(this)) {
            Log.d(TAG, "Requesting overlay permission")
            PermissionHelper.requestOverlayPermission(this)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Stop call detection when app is destroyed
        callDetectionManager.stopCallDetection()
    }
}
