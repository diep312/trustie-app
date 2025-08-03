package com.example.trustie.data.api

import com.example.trustie.data.model.CallHistoryItem
import retrofit2.http.GET

interface CallApiService {
    @GET("call-history")
    suspend fun getCallHistory(): List<CallHistoryItem>
}