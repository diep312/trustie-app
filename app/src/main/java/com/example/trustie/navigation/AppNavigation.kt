package com.example.trustie.navigation

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Text
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.trustie.data.GlobalStateManager
import com.example.trustie.navigation.NavigationManager.safeNavigate
import com.example.trustie.navigation.NavigationManager.safePopBackStack
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
import com.example.trustie.ui.screen.audiocallrecorder.AudioRecorderScreen
import com.example.trustie.ui.screen.imagedetection.ImageVerificationScreen
import com.example.trustie.ui.screen.scamresult.ScamResultScreen
import com.example.trustie.ui.screen.notification.NotificationScreen
import com.example.trustie.ui.screen.qrscreen.QrScannerScreen

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
                    navController.safeNavigate(Screen.PhoneInput.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.safeNavigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                isLoggedIn = isLoggedIn.isAuthenticated
            )
        }

        composable(Screen.PhoneInput.route) {
            PhoneInputScreen(
                onNavigateToOTP = {
                    navController.safeNavigate(Screen.OTPInput.route)
                },
                viewModel = authViewModel
            )
        }

        composable(Screen.OTPInput.route) {
            OTPInputScreen(
                onNavigateToHome = {
                    navController.safeNavigate(Screen.Home.route) {
                        popUpTo(Screen.PhoneInput.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.safePopBackStack()
                },
                viewModel = authViewModel
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onFeatureClick = { feature ->
                    when (feature.title) {
                        "Lịch sử gọi" -> {
                            navController.safeNavigate(Screen.CallHistory.route)
                        }
                        "Báo cáo số" -> {
                            navController.safeNavigate(Screen.ReportPhone.route)
                        }
                        "Kết nối người thân" -> {
                            navController.safeNavigate(Screen.ConnectRelatives.route)
                        }
                        "Kiểm tra ảnh" -> {
                            navController.safeNavigate(Screen.CheckImage.route)
                        }
                        "Kiểm tra số" -> {
                            navController.safeNavigate(Screen.CheckPhone.route)
                        }
                        "Nhận diện cuộc gọi lừa đảo" -> {
                            if (authViewModel.isUserElderly()) {
                                navController.safeNavigate(Screen.AudioRecording.route)
                            } else {
                                navController.safeNavigate(Screen.QRScan.route)
                            }
                        }
                    }
                },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.safeNavigate(Screen.PhoneInput.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNotificationClick = {
                    navController.safeNavigate(Screen.Notifications.route)
                }
            )
        }

        composable(Screen.CallHistory.route) {
            CallHistoryScreen(
                onBackClick = {
                    navController.safePopBackStack()
                }
            )
        }

        composable(Screen.CheckPhone.route) {
            CheckPhoneScreen(
                onBackClick = {
                    navController.safePopBackStack()
                }
            )
        }

        composable(Screen.ConnectRelatives.route) {
            ConnectRelativesScreen(
                onBackClick = {
                    navController.safePopBackStack()
                }
            )
        }

        composable(Screen.ReportPhone.route) {
            ReportPhoneScreen(
                onBackClick = {
                    navController.safePopBackStack()
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
                    navController.safePopBackStack()
                },
                onDeclineCall = {
                    navController.safePopBackStack()
                }
            )
        }

        composable(Screen.CheckImage.route) {
            ImageVerificationScreen(
                onBackClick = {
                    navController.safePopBackStack()
                },
                onNavigateToScamResult = { verificationResponse ->
                    navController.safeNavigate(Screen.ScamResult.route)
                }
            )
        }

        composable(Screen.ScamResult.route) {
            ScamResultScreen(
                onBackClick = {
                    navController.safePopBackStack()
                }
            )
        }

        composable(Screen.AudioRecording.route) {
            AudioRecorderScreen(
                onBackClick = {
                    navController.safePopBackStack()
                },
                onNavigateToScamResult = { verificationResponse ->
                    navController.safeNavigate(Screen.ScamResult.route)
                }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationScreen(
                onBackClick = {
                    navController.safePopBackStack()
                }
            )
        }

        composable(Screen.QRScan.route) {
            QrScannerScreen(
                onBackClick = {
                    navController.safePopBackStack()
                }
            )
        }
    }
}