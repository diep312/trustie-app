package com.example.trustie.ui.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.lifecycle.lifecycleScope
import com.example.trustie.ui.screen.auth.AuthState
import com.example.trustie.ui.screen.auth.AuthViewModel
import com.example.trustie.ui.screen.home.HomeScreen
import com.example.trustie.ui.screen.splash.SplashScreen
import com.example.trustie.ui.theme.TrustieTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
abstract class BaseAuthenticatedActivity : ComponentActivity() {
    
    private val authViewModel: AuthViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            TrustieTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthenticationWrapper(
                        authViewModel = authViewModel,
                        onAuthenticated = { user ->
                            // User is authenticated, show the main content
                            showMainContent(user)
                        },
                        onUnauthenticated = {
                            // User is not authenticated, show login
                            showLoginScreen()
                        }
                    )
                }
            }
        }
    }
    
    @Composable
    private fun AuthenticationWrapper(
        authViewModel: AuthViewModel,
        onAuthenticated: (com.example.trustie.data.model.datamodel.User) -> Unit,
        onUnauthenticated: () -> Unit
    ) {
        val authState by authViewModel.authState.collectAsState()
        val isLoading by authViewModel.isLoading.collectAsState()
        
        LaunchedEffect(authState) {
            when (authState) {
                is AuthState.Initial -> {
                    // Still checking auth status, show loading
                }
                is AuthState.Authenticated -> {
                    onAuthenticated((authState as AuthState.Authenticated).user)
                }
                is AuthState.Unauthenticated -> {
                    onUnauthenticated()
                }
                is AuthState.Error -> {
                    // Handle error, maybe show error screen or retry
                    onUnauthenticated()
                }
                is AuthState.Success -> {
                    // Handle success state
                }
            }
        }
        
        if (isLoading || authState is AuthState.Initial) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
    
    @Composable
    private fun showMainContent(user: com.example.trustie.data.model.datamodel.User) {
        // Show the main app content
        MainContent()
    }
    
    @Composable
    private fun showLoginScreen() {
        // Show login screen with auto-login for now
        LaunchedEffect(Unit) {
            authViewModel.loginWithFixedUser()
        }
        
        // Show splash screen while auto-logging in
        SplashScreen(
            onNavigateToLogin = { /* Handle navigation to login */ },
            onNavigateToHome = { /* Handle navigation to home */ },
            isLoggedIn = false
        )
    }

    @Composable
    protected open fun MainContent() {
        HomeScreen(
            onNavigateToLogin = { /* Handle navigation to login */ },
            onNavigateToHome = { /* Handle navigation to home */ },
            isLoggedIn = true,
            onLogoutClick = { authViewModel.logout() }
        )
    }
    

    @Composable
    protected open fun LoginContent() {
        SplashScreen(
            onNavigateToLogin = { /* Handle navigation to login */ },
            onNavigateToHome = { /* Handle navigation to home */ },
            isLoggedIn = false
        )
    }
} 