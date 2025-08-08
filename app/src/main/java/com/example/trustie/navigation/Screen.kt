package com.example.trustie.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object PhoneInput : Screen("phone_input")
    object OTPInput : Screen("otp_input")
    object Home : Screen("home")
    object CallHistory : Screen("call_history")
    object ReportPhone : Screen("report_phone")
    object CheckPhone : Screen("check_phone")
    object CheckImage : Screen("check_image")
    object ConnectRelatives : Screen("connect_relatives")
    object QRScanner : Screen("qr_scanner")
    object Notifications : Screen("notifications")
    object ScamResult : Screen("scam_result")
    object IncomingCallAlert : Screen("incoming_call_alert/{phoneNumber}/{isSuspicious}") {
        fun createRoute(phoneNumber: String, isSuspicious: Boolean) = "incoming_call_alert/$phoneNumber/$isSuspicious"
    }
}
