package com.example.trustie.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.trustie.ui.screen.auth.AuthViewModel
import com.example.trustie.ui.screen.splash.SplashScreen
import com.example.trustie.ui.screen.auth.PhoneInputScreen
import com.example.trustie.ui.screen.auth.OTPInputScreen
import com.example.trustie.ui.screen.home.HomeScreen
import com.example.trustie.ui.screen.callhistory.CallHistoryScreen
import com.example.trustie.ui.screen.checkphone.CheckPhoneScreen
import com.example.trustie.ui.screen.connect.ConnectRelativesScreen
import com.example.trustie.ui.screen.report.ReportPhoneScreen
import com.example.trustie.ui.screen.alert.IncomingCallAlertScreen
import com.example.trustie.ui.screen.imagedetection.ImageVerificationScreen
import com.example.trustie.ui.screen.scamresult.ScamResultScreen
import com.example.trustie.ui.screen.notification.NotificationScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isLoggedIn by authViewModel.authState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.PhoneInput.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                isLoggedIn = isLoggedIn.isAuthenticated
            )
        }

        composable(Screen.PhoneInput.route) {
            PhoneInputScreen(
                onNavigateToOTP = {
                    navController.navigate(Screen.OTPInput.route)
                },
                viewModel = authViewModel
            )
        }

        composable(Screen.OTPInput.route) {
            OTPInputScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.PhoneInput.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = authViewModel
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onFeatureClick = { feature ->
                    when (feature.title) {
                        "Lịch sử gọi" -> {
                            navController.navigate(Screen.CallHistory.route)
                        }
                        "Báo cáo số" -> {
                            navController.navigate(Screen.ReportPhone.route)
                        }
                        "Kết nối người thân" -> {
                            navController.navigate(Screen.ConnectRelatives.route)
                        }
                        "Kiểm tra ảnh" -> {
                            navController.navigate(Screen.CheckImage.route)
                        }
                        "Kiểm tra số" -> {
                            navController.navigate(Screen.CheckPhone.route)
                        }
                    }
                },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.PhoneInput.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNotificationClick = {
                    navController.navigate(Screen.Notifications.route)
                }
            )
        }

        composable(Screen.CallHistory.route) {
            CallHistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.CheckPhone.route) {
            CheckPhoneScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ConnectRelatives.route) {
            ConnectRelativesScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ReportPhone.route) {
            ReportPhoneScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.IncomingCallAlert.route,
            arguments = listOf(
                navArgument("phoneNumber") { type = NavType.StringType },
                navArgument("isSuspicious") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: "Unknown"
            val isSuspicious = backStackEntry.arguments?.getBoolean("isSuspicious") ?: false
            IncomingCallAlertScreen(
                phoneNumber = phoneNumber,
                isSuspicious = isSuspicious,
                onAcceptCall = {
                    navController.popBackStack()
                },
                onDeclineCall = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.CheckImage.route) {
            ImageVerificationScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToScamResult = { verificationResponse ->
                    navController.navigate(Screen.ScamResult.route)
                }
            )
        }

        composable(Screen.ScamResult.route) {
            ScamResultScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}