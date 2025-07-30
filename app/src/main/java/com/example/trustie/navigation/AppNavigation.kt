




package com.example.trustie.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import com.example.trustie.ui.screen.CallHistoryScreen
import com.example.trustie.ui.screen.HomeScreen
import androidx.compose.material3.Text
import com.example.trustie.ui.screen.CheckPhoneScreen
import com.example.trustie.ui.screen.ConnectRelativesScreen

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
                        "Kết nối với người thân" -> {
                            navController.navigate(Screen.ConnectRelatives.route)
                        }
                    }
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
        composable(Screen.CheckPhone.route) { // <-- Thêm composable cho CheckPhoneScreen
            CheckPhoneScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.ConnectRelatives.route) { // <-- Thêm composable cho ConnectRelativesScreen
            ConnectRelativesScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.PhoneAlert.route) { Text("Màn hình Cảnh báo SĐT") }
        composable(Screen.CheckWeb.route) { Text("Màn hình Kiểm tra web") }
    }
}
