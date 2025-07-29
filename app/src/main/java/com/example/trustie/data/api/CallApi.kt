package com.example.trustie.data.api

import com.example.trustie.ui.model.CallHistoryResponse
import retrofit2.http.GET

interface CallApi {
    @GET("call-history") // Thay thế bằng endpoint API thực tế của bạn
    suspend fun getCallHistory(): CallHistoryResponse
}