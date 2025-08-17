//package com.example.trustie.data.notification
//package com.example.trustie.data.notification
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.graphics.Color
//import android.graphics.PixelFormat
//import android.os.Build
//import android.provider.Settings
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.WindowManager
//import android.widget.Button
//import android.widget.TextView
//import android.util.Log
//import com.example.trustie.R
//
//class FullScreenOverlayManager(private val context: Context) {
//
//    companion object {
//        private const val TAG = "FullScreenOverlayManager"
//    }
//
//    private var overlayView: View? = null
//    private val windowManager: WindowManager by lazy {
//        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//    }
//
//    fun showRedScreenOverlay(result: CallRiskResult) {
//        if (!canDrawOverlays()) {
//            Log.w(TAG, "Cannot draw overlays, permission not granted")
//            return
//        }
//
//        dismissOverlay() // Remove any existing overlay
//
//        try {
//            overlayView = createOverlayView(result, Color.RED, "🚨 NGUY HIỂM 🚨")
//            val params = createOverlayParams()
//            windowManager.addView(overlayView, params)
//            Log.d(TAG, "Red screen overlay shown for ${result.phoneNumber}")
//        } catch (e: Exception) {
//            Log.e(TAG, "Failed to show red screen overlay", e)
//        }
//    }
//
//    fun showYellowScreenOverlay(result: CallRiskResult) {
//        if (!canDrawOverlays()) {
//            Log.w(TAG, "Cannot draw overlays, permission not granted")
//            return
//        }
//
//        dismissOverlay() // Remove any existing overlay
//
//        try {
//            overlayView = createOverlayView(result, Color.YELLOW, "⚠️ THẬN TRỌNG ⚠️")
//            val params = createOverlayParams()
//            windowManager.addView(overlayView, params)
//            Log.d(TAG, "Yellow screen overlay shown for ${result.phoneNumber}")
//        } catch (e: Exception) {
//            Log.e(TAG, "Failed to show yellow screen overlay", e)
//        }
//    }
//
//    private fun createOverlayView(result: CallRiskResult, backgroundColor: Int, warningTitle: String): View {
//        val inflater = LayoutInflater.from(context)
//        val overlayView = inflater.inflate(R.layout.overlay_call_warning, null)
//
//        // Set background color with some transparency
//        val alphaColor = Color.argb(
//            220, // Alpha (0-255, 220 = ~86% opacity)
//            Color.red(backgroundColor),
//            Color.green(backgroundColor),
//            Color.blue(backgroundColor)
//        )
//        overlayView.setBackgroundColor(alphaColor)
//
//        // Set warning title
//        overlayView.findViewById<TextView>(R.id.tvWarningTitle)?.apply {
//            text = warningTitle
//            setTextColor(if (backgroundColor == Color.YELLOW) Color.BLACK else Color.WHITE)
//        }
//
//        // Set phone number
//        overlayView.findViewById<TextView>(R.id.tvPhoneNumber)?.apply {
//            text = result.phoneNumber
//            setTextColor(if (backgroundColor == Color.YELLOW) Color.BLACK else Color.WHITE)
//        }
//
//        // Set risk message
//        overlayView.findViewById<TextView>(R.id.tvRiskMessage)?.apply {
//            text = result.getDetailedMessage()
//            setTextColor(if (backgroundColor == Color.YELLOW) Color.BLACK else Color.WHITE)
//        }
//
//        // Set up action buttons
//        setupActionButtons(overlayView, result, backgroundColor)
//
//        return overlayView
//    }
//
//    private fun setupActionButtons(overlayView: View, result: CallRiskResult, backgroundColor: Int) {
//        val textColor = if (backgroundColor == Color.YELLOW) Color.BLACK else Color.WHITE
//
//        // Answer/Accept button (for medium risk) or Emergency Contact button (for high risk)
//        overlayView.findViewById<Button>(R.id.btnAnswer)?.apply {
//            if (result.riskLevel == RiskLevel.HIGH) {
//                text = "Gọi Khẩn Cấp"
//                setTextColor(Color.WHITE)
//                setBackgroundColor(Color.rgb(139, 0, 0)) // Dark red
//                setOnClickListener {
//                    // Call emergency contact or show emergency options
//                    handleEmergencyAction()
//                    dismissOverlay()
//                }
//            } else {
//                text = "Trả Lời Cẩn Thận"
//                setTextColor(Color.BLACK)
//                setBackgroundColor(Color.rgb(255, 255, 224)) // Light yellow
//                setOnClickListener {
//                    // Dismiss overlay and let user answer with caution
//                    dismissOverlay()
//                }
//            }
//        }
//
//        // Block/Reject button
//        overlayView.findViewById<Button>(R.id.btnReject)?.apply {
//            text = if (result.riskLevel == RiskLevel.HIGH) "Chặn & Báo Cáo" else "Từ Chối"
//            setTextColor(Color.WHITE)
//            setBackgroundColor(Color.rgb(139, 0, 0)) // Dark red
//            setOnClickListener {
//                handleRejectCall(result)
//                dismissOverlay()
//            }
//        }
//
//        // Dismiss overlay button
//        overlayView.findViewById<Button>(R.id.btnDismiss)?.apply {
//            text = "Đóng Cảnh Báo"
//            setTextColor(textColor)
//            setBackgroundColor(Color.TRANSPARENT)
//            setOnClickListener {
//                dismissOverlay()
//            }
//        }
//
//        // More info button
//        overlayView.findViewById<Button>(R.id.btnMoreInfo)?.apply {
//            text = "Thông Tin Chi Tiết"
//            setTextColor(textColor)
//            setBackgroundColor(Color.TRANSPARENT)
//            setOnClickListener {
//                showMoreInfo(result)
//            }
//        }
//    }
//
//    private fun createOverlayParams(): WindowManager.LayoutParams {
//        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//        } else {
//            @Suppress("DEPRECATION")
//            WindowManager.LayoutParams.TYPE_PHONE
//        }
//
//        return WindowManager.LayoutParams(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.MATCH_PARENT,
//            type,
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
//                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
//                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
//                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
//                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
//            PixelFormat.TRANSLUCENT
//        ).apply {
//            gravity = Gravity.CENTER
//        }
//    }
//
//    private fun handleEmergencyAction() {
//        // Implement emergency contact calling or show emergency options
//        try {
//            val emergencyIntent = Intent(Intent.ACTION_CALL).apply {
//                data = android.net.Uri.parse("tel:113") // Vietnam emergency number
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            }
//            context.startActivity(emergencyIntent)
//        } catch (e: Exception) {
//            Log.e(TAG, "Failed to initiate emergency call", e)
//            // Fallback: show emergency contacts screen
//            showEmergencyContacts()
//        }
//    }
//
//    private fun handleRejectCall(result: CallRiskResult) {
//        // Implement call rejection and reporting logic
//        try {
//            // Send broadcast to reject the call
//            val rejectIntent = Intent("com.example.trustie.REJECT_CALL").apply {
//                putExtra("phone_number", result.phoneNumber)
//                putExtra("risk_level", result.riskLevel.name)
//            }
//            context.sendBroadcast(rejectIntent)
//
//            // If high risk, also report the number
//            if (result.riskLevel == RiskLevel.HIGH) {
//                reportScamNumber(result.phoneNumber)
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Failed to reject call", e)
//        }
//    }
//
//    private fun showMoreInfo(result: CallRiskResult) {
//        try {
//            val intent = Intent().apply {
//                setClassName(context, "com.example.trustie.ui.screen.checkphone.CheckPhoneActivity")
//                putExtra("phone_number", result.phoneNumber)
//                putExtra("risk_result", result) // You may need to make CallRiskResult Parcelable
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//            }
//            context.startActivity(intent)
//            dismissOverlay()
//        } catch (e: Exception) {
//            Log.e(TAG, "Failed to show more info", e)
//        }
//    }
//
//    private fun showEmergencyContacts() {
//        // Implement showing emergency contacts
//        Log.d(TAG, "Should show emergency contacts")
//    }
//
//    private fun reportScamNumber(phoneNumber: String) {
//        // Implement scam number reporting
//        Log.d(TAG, "Reporting scam number: $phoneNumber")
//    }
//
//    private fun canDrawOverlays(): Boolean {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Settings.canDrawOverlays(context)
//        } else {
//            true
//        }
//    }
//
//    fun dismissOverlay() {
//        overlayView?.let { view ->
//            try {
//                windowManager.removeView(view)
//                overlayView = null
//                Log.d(TAG, "Overlay dismissed")
//            } catch (e: Exception) {
//                Log.e(TAG, "Failed to dismiss overlay", e)
//            }
//        }
//    }
//
//    fun isOverlayShowing(): Boolean {
//        return overlayView != null
//    }
//}