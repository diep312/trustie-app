package com.example.trustie.repository.reportrepo

import android.util.Log
import com.example.trustie.data.model.ReportResponse
import kotlinx.coroutines.delay

class ReportRepository {
    suspend fun submitPhoneReport(phoneNumber: String, reason: String): ReportResponse {
        return try {
            Log.d("ReportRepoDebug", "Submitting phone report for $phoneNumber - $reason")
            delay(1000) // Simulate API call
            
            ReportResponse(
                success = true,
                message = "Báo cáo đã được gửi thành công"
            )
        } catch (e: Exception) {
            Log.e("ReportRepoDebug", "Error in submitPhoneReport: ${e.message}", e)
            ReportResponse(
                success = false,
                message = "Lỗi khi gửi báo cáo: ${e.message}"
            )
        }
    }
}