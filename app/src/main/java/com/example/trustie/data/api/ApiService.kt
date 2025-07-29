package com.example.trustie.data.api

import com.example.trustie.ui.model.CallHistoryResponse
import kotlinx.coroutines.delay

class ApiService {

    // Base URL for your backend API
    private val baseUrl = "https://your-api-domain.com/api"

    suspend fun getCallHistory(): CallHistoryResponse {
        // Simulate API call
        delay(1000)

        throw NotImplementedError("Implement actual API call here")
    }

    suspend fun reportSuspiciousCall(phoneNumber: String): Boolean {
        delay(500)
        // TODO: Implement actual API call
        return true
    }

    suspend fun blockNumber(phoneNumber: String): Boolean {
        delay(500)
        // TODO: Implement actual API call
        return true
    }
}



//package com.example.trustie.data.api
//
//import com.example.trustie.ui.model.CallHistoryResponse
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import okhttp3.OkHttpClient // Import OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor // Import HttpLoggingInterceptor
//
//class ApiService {
//
//    private val baseUrl = "https://api.your-backend.com/api/" // <-- Đảm bảo URL này đúng!
//
//    // Tạo HttpLoggingInterceptor để log request/response
//    private val loggingInterceptor = HttpLoggingInterceptor().apply {
//        level = HttpLoggingInterceptor.Level.BODY // Log toàn bộ body của request/response
//    }
//
//    // Tạo OkHttpClient với logging interceptor
//    private val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor(loggingInterceptor)
//        .build()
//
//    private val retrofit: Retrofit = Retrofit.Builder()
//        .baseUrl(baseUrl)
//        .client(okHttpClient) // Sử dụng OkHttpClient đã cấu hình
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    private val callApi: CallApi = retrofit.create(CallApi::class.java)
//
//    suspend fun getCallHistory(): CallHistoryResponse {
//        return callApi.getCallHistory()
//    }
//
//    suspend fun reportSuspiciousCall(phoneNumber: String): Boolean {
//        // TODO: Implement actual API call for reporting
//        return true
//    }
//
//    suspend fun blockNumber(phoneNumber: String): Boolean {
//        // TODO: Implement actual API call for blocking
//        return true
//    }
//}