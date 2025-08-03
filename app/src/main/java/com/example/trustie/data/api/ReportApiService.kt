package com.example.trustie.data.api

import android.util.Log
import com.example.trustie.data.model.ReportRequest
import com.example.trustie.data.model.ReportResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

// Định nghĩa interface cho Retrofit API
interface ReportApi {
    @POST("report/phone") // Endpoint để báo cáo số điện thoại
    suspend fun submitReport(@Body request: ReportRequest): ReportResponse
}

class ReportApiService {
    private val baseUrl = "https://your-real-api.com/api/" // Thay thế bằng URL API thật của bạn, kết thúc bằng dấu /

    private val api: ReportApi

    init {
        // Thêm Logging Interceptor để xem log của request/response
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Log toàn bộ body của request/response
        }

        // Cấu hình OkHttpClient
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging) // Thêm interceptor vào client
            .connectTimeout(30, TimeUnit.SECONDS) // Thời gian chờ kết nối
            .readTimeout(30, TimeUnit.SECONDS)    // Thời gian chờ đọc dữ liệu
            .writeTimeout(30, TimeUnit.SECONDS)   // Thời gian chờ ghi dữ liệu
            .build()


        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient) // Sử dụng OkHttpClient đã cấu hình
            .addConverterFactory(GsonConverterFactory.create()) // Sử dụng Gson để chuyển đổi JSON
            .build()

        api = retrofit.create(ReportApi::class.java)
    }

    suspend fun submitReport(request: ReportRequest): ReportResponse {
        return try {
            Log.d("ReportApiDebug", "Submitting report for ${request.phoneNumber} with reason: ${request.reason}")
            // Gọi API thực tế thông qua Retrofit interface
            api.submitReport(request)
        } catch (e: Exception) {
            Log.e("ReportApiDebug", "Error submitting report: ${e.message}", e)
            ReportResponse(
                success = false,
                message = "Lỗi kết nối: ${e.message}"
            )
        }
    }
}
