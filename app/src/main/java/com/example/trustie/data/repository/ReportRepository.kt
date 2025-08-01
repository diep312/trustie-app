package com.example.trustie.data.repository

import android.util.Log
import com.example.trustie.data.api.ReportApiService
import com.example.trustie.ui.model.ReportRequest
import com.example.trustie.ui.model.ReportResponse

class ReportRepository(
    private val apiService: ReportApiService = ReportApiService()
) {
    suspend fun submitPhoneReport(phoneNumber: String, reason: String): ReportResponse {
        return try {
            Log.d("ReportRepoDebug", "Submitting phone report for $phoneNumber - $reason")
            val request = ReportRequest(phoneNumber, reason)
            apiService.submitReport(request)
        } catch (e: Exception) {
            Log.e("ReportRepoDebug", "Error in submitPhoneReport: ${e.message}", e)
            ReportResponse(
                success = false,
                message = "Lỗi khi gửi báo cáo: ${e.message}"
            )
        }
    }
}
