package com.example.trustie.data.repository

import android.util.Log
import com.example.trustie.data.api.PhoneCheckApiService
import com.example.trustie.data.model.PhoneCheckItem
import com.example.trustie.data.model.PhoneCheckResponse
import kotlinx.coroutines.delay

class PhoneCheckRepository(
    private val apiService: PhoneCheckApiService = PhoneCheckApiService()
) {
    suspend fun checkPhoneNumber(phoneNumber: String): PhoneCheckResponse {
        return try {
            Log.d("PhoneCheckDebug", "Checking phone number: $phoneNumber")
            delay(1500) // Mô phỏng độ trễ mạng

            // Dữ liệu giả để test UI
            val mockResult = when {
                phoneNumber.contains("123") -> PhoneCheckItem(
                    phoneNumber = phoneNumber,
                    isSuspicious = true,
                    riskLevel = "HIGH",
                    reportCount = 15,
                    lastReported = "2023-10-26",
                    description = "Số này đã được báo cáo là lừa đảo nhiều lần"
                )
                phoneNumber.contains("456") -> PhoneCheckItem(
                    phoneNumber = phoneNumber,
                    isSuspicious = true,
                    riskLevel = "MEDIUM",
                    reportCount = 5,
                    lastReported = "2023-10-25",
                    description = "Có một vài báo cáo về số này"
                )
                else -> PhoneCheckItem(
                    phoneNumber = phoneNumber,
                    isSuspicious = false,
                    riskLevel = "LOW",
                    reportCount = 0,
                    lastReported = null,
                    description = "Số điện thoại này có vẻ an toàn"
                )
            }

            PhoneCheckResponse(
                success = true,
                data = mockResult
            )
        } catch (e: Exception) {
            Log.e("PhoneCheckDebug", "Error checking phone number: ${e.message}", e)
            PhoneCheckResponse(
                success = false,
                data = null,
                message = e.message
            )
        }
    }
}