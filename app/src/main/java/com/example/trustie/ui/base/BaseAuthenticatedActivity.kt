package com.example.trustie.ui.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import android.view.View
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trustie.ui.screen.auth.AuthState
import com.example.trustie.ui.screen.auth.AuthViewModel
import com.example.trustie.ui.screen.home.HomeScreen
import com.example.trustie.ui.screen.splash.SplashScreen
import com.example.trustie.ui.theme.TrustieTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
abstract class BaseAuthenticatedActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configure status bar for light theme
        configureStatusBar()
        
        setContent {
            TrustieTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthenticationWrapper()
                }
            }
        }
    }

    private fun configureStatusBar() {
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set background color
        val backgroundColor = Color(0xFFFDF2E9).toArgb()
        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor

        // Set dark icons (light theme = dark icons)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        insetsController.isAppearanceLightNavigationBars = true
    }
    
    @Composable
    private fun AuthenticationWrapper() {
        val authViewModel: AuthViewModel = hiltViewModel()
        val authState by authViewModel.authState.collectAsState()
        val isLoading by authViewModel.isLoading.collectAsState()
        
        when {
            isLoading || authState is AuthState.Initial -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            authState is AuthState.Authenticated -> {
                MainContent(authViewModel)
            }
            authState is AuthState.Unauthenticated -> {
                LoginContent(authViewModel)
            }
            authState is AuthState.Error -> {
                // Handle error, show login screen
                LoginContent(authViewModel)
            }
            else -> {
                // Default to login screen
                LoginContent(authViewModel)
            }
        }
    }
    
    @Composable
    protected open fun MainContent(authViewModel: AuthViewModel) {
        HomeScreen(
            onFeatureClick = { /* Handle feature click */ },
            onLogoutClick = { authViewModel.logout() },
            onNotificationClick = { /* Handle notification click */ }
        )
    }
    

    @Composable
    protected open fun LoginContent(authViewModel: AuthViewModel) {
        // Auto-login for now - only if not already loading and not authenticated
        LaunchedEffect(Unit) {
            val currentState = authViewModel.authState.value
            if (currentState is AuthState.Unauthenticated || currentState is AuthState.Error) {
                android.util.Log.d("BaseAuthenticatedActivity", "LoginContent - auto-login triggered")
                authViewModel.loginWithFixedUser()
            }
        }
        
        SplashScreen(
            onNavigateToLogin = { /* Handle navigation to login */ },
            onNavigateToHome = { /* Handle navigation to home */ },
            isLoggedIn = false
        )
    }
} 