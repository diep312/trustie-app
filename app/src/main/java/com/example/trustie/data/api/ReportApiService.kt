package com.example.trustie.data.api

import com.example.trustie.data.model.request.PhoneReportRequest
import com.example.trustie.data.model.request.SMSReportRequest
import com.example.trustie.data.model.request.WebsiteReportRequest
import com.example.trustie.data.model.response.ReportResponse
import retrofit2.http.*

interface ReportApiService {
    
    @POST("reports/phone")
    suspend fun reportPhone(@Body request: PhoneReportRequest): ReportResponse
    
    @POST("reports/website")
    suspend fun reportWebsite(@Body request: WebsiteReportRequest): ReportResponse
    
    @POST("reports/sms")
    suspend fun reportSMS(@Body request: SMSReportRequest): ReportResponse
}
