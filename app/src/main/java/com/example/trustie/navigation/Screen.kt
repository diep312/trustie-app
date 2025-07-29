package com.example.trustie.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CallHistory : Screen("call_history")
    object PhoneAlert : Screen("phone_alert")
    object CheckPhone : Screen("check_phone")
    object CheckWeb : Screen("check_web")
    object ConnectRelatives : Screen("connect_relatives")
}