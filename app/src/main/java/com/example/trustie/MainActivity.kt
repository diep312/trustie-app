package com.example.trustie

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.trustie.navigation.AppNavigation
import com.example.trustie.navigation.Screen
import com.example.trustie.ui.theme.TrustieTheme
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivityDebug", "onCreate called.")
        setContent {
            TrustieTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                LaunchedEffect(intent) {
                    Log.d("MainActivityDebug", "LaunchedEffect triggered with intent: ${intent?.action}")
                    handleIntent(intent, navController)
                }
                this@MainActivity.addOnNewIntentListener { newIntent ->
                    Log.d("MainActivityDebug", "onNewIntentListener triggered with new intent: ${newIntent.action}")
                    handleIntent(newIntent, navController)
                }
                AppNavigation(navController = navController)
            }
        }
    }

    private fun handleIntent(intent: Intent?, navController: androidx.navigation.NavController) {
        Log.d("MainActivityDebug", "handleIntent called. Intent data: ${intent?.data}, host: ${intent?.data?.host}")
        if (intent?.action == Intent.ACTION_VIEW && intent.data?.host == "call_alert") {
            val phoneNumber = intent.getStringExtra("phoneNumber")
            val isSuspicious = intent.getBooleanExtra("isSuspicious", false)
            Log.d("MainActivityDebug", "Received call alert intent: phoneNumber=$phoneNumber, isSuspicious=$isSuspicious")
            if (phoneNumber != null) {
                navController.navigate(Screen.IncomingCallAlert.createRoute(phoneNumber, isSuspicious)) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    launchSingleTop = true
                }
                Log.d("MainActivityDebug", "Navigated to IncomingCallAlertScreen.")
            } else {
                Log.d("MainActivityDebug", "Phone number is null in alert intent.")
            }
            intent.replaceExtras(Bundle())
            intent.data = null
        } else {
            Log.d("MainActivityDebug", "Intent is not a call alert intent. Action: ${intent?.action}")
        }
    }
}
