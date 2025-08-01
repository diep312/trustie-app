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
import com.example.trustie.ui.screen.*
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.trustie.ui.viewmodel.AuthViewModel
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Khởi tạo AuthViewModel một lần ở đây
    val authViewModel: AuthViewModel = viewModel { AuthViewModel(context) }
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

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
                isLoggedIn = isLoggedIn
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
                        "Kiểm tra Số" -> {
                            navController.navigate(Screen.CheckPhone.route)
                        }
                        "Kiểm tra ảnh" -> {
                            navController.navigate(Screen.CheckImage.route)
                        }
                        "Kết nối" -> {
                            navController.navigate(Screen.ConnectRelatives.route)
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

        composable(Screen.ReportPhone.route) { // Thêm composable cho màn hình báo cáo
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
                    // TODO: Triển khai logic chấp nhận cuộc gọi thực tế
                    navController.popBackStack() // Quay lại màn hình trước đó sau khi chấp nhận
                },
                onDeclineCall = {
                    // TODO: Triển khai logic từ chối cuộc gọi thực tế
                    navController.popBackStack() // Quay lại màn hình trước đó sau khi từ chối
                }
            )
        }
        composable(Screen.CheckImage.route) {
            ImageVerificationScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Notifications.route) { // Thêm màn hình thông báo
            NotificationScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

    }
}
