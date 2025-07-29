




package com.example.trustie.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import com.example.trustie.ui.screen.CallHistoryScreen
import com.example.trustie.ui.screen.HomeScreen
import androidx.compose.material3.Text

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onFeatureClick = { feature ->
                    when (feature.title) {
                        "Lịch sử cuộc gọi" -> {
                            navController.navigate(Screen.CallHistory.route)
                        }
                        "Cảnh báo SĐT" -> {
                            navController.navigate(Screen.PhoneAlert.route)
                        }
                        "Kiểm tra SĐT" -> {
                            navController.navigate(Screen.CheckPhone.route)
                        }
                        "Kiểm tra web" -> {
                            navController.navigate(Screen.CheckWeb.route)
                        }
                        "Kết nối với\nnguời thân" -> {
                            navController.navigate(Screen.ConnectRelatives.route)
                        }
                    }
                }
            )
        }

        composable(Screen.CallHistory.route) {
            CallHistoryScreen(
                onBackClick = {
                    navController.popBackStack() // <-- Truyền lambda để quay lại
                }
            )
        }

        // Các màn hình placeholder khác
        composable(Screen.PhoneAlert.route) { Text("Màn hình Cảnh báo SĐT") }
        composable(Screen.CheckPhone.route) { Text("Màn hình Kiểm tra SĐT") }
        composable(Screen.CheckWeb.route) { Text("Màn hình Kiểm tra web") }
        composable(Screen.ConnectRelatives.route) { Text("Màn hình Kết nối với người thân") }
    }
}
