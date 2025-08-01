
package com.example.trustie

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.trustie.navigation.AppNavigation
import com.example.trustie.navigation.Screen
import com.example.trustie.ui.theme.TrustieTheme
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivityDebug", "onCreate called.") // Log A
        setContent {
            TrustieTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                LaunchedEffect(intent) {
                    Log.d("MainActivityDebug", "LaunchedEffect triggered with intent: ${intent?.action}") // Log B
                    handleIntent(intent, navController)
                }


                this@MainActivity.addOnNewIntentListener { newIntent ->
                    Log.d("MainActivityDebug", "onNewIntentListener triggered with new intent: ${newIntent.action}") // Log C
                    handleIntent(newIntent, navController)
                }

                AppNavigation(navController = navController)
            }
        }
    }

    private fun handleIntent(intent: Intent?, navController: androidx.navigation.NavController) {
        Log.d("MainActivityDebug", "handleIntent called. Intent data: ${intent?.data}, host: ${intent?.data?.host}") // Log D

        if (intent?.action == Intent.ACTION_VIEW && intent.data?.host == "call_alert") {
            val phoneNumber = intent.getStringExtra("phoneNumber")
            val isSuspicious = intent.getBooleanExtra("isSuspicious", false)

            Log.d("MainActivityDebug", "Received call alert intent: phoneNumber=$phoneNumber, isSuspicious=$isSuspicious") // Log E

            if (phoneNumber != null) {
                navController.navigate(Screen.IncomingCallAlert.createRoute(phoneNumber, isSuspicious)) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    launchSingleTop = true
                }
                Log.d("MainActivityDebug", "Navigated to IncomingCallAlertScreen.") // Log F
            } else {
                Log.d("MainActivityDebug", "Phone number is null in alert intent.") // Log G
            }

            intent.replaceExtras(Bundle())
            intent.data = null
        } else {
            Log.d("MainActivityDebug", "Intent is not a call alert intent. Action: ${intent?.action}") // Log H
        }
    }
}

