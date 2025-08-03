package com.example.trustie.data.api

import com.example.trustie.data.model.request.CreateAlertRequest
import com.example.trustie.data.model.response.AlertResponse
import retrofit2.http.*

interface AlertApiService {
    
    @GET("alerts/user/{user_id}")
    suspend fun getUserAlerts(
        @Path("user_id") userId: Int,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("unread_only") unreadOnly: Boolean = false
    ): List<AlertResponse>
    
    @GET("alerts/user/{user_id}/unread-count")
    suspend fun getUnreadAlertCount(@Path("user_id") userId: Int): Map<String, Any>
    
    @PUT("alerts/{alert_id}/read")
    suspend fun markAlertAsRead(
        @Path("alert_id") alertId: Int,
        @Query("user_id") userId: Int
    ): Map<String, Any>
    
    @PUT("alerts/{alert_id}/acknowledge")
    suspend fun acknowledgeAlert(
        @Path("alert_id") alertId: Int,
        @Query("user_id") userId: Int
    ): Map<String, Any>
    
    @DELETE("alerts/{alert_id}")
    suspend fun deleteAlert(
        @Path("alert_id") alertId: Int,
        @Query("user_id") userId: Int
    ): Map<String, Any>
    
    @GET("alerts/user/{user_id}/severity/{severity}")
    suspend fun getAlertsBySeverity(
        @Path("user_id") userId: Int,
        @Path("severity") severity: String
    ): List<AlertResponse>
    
    @GET("alerts/user/{user_id}/critical")
    suspend fun getCriticalAlerts(@Path("user_id") userId: Int): List<AlertResponse>
    
    @POST("alerts/create")
    suspend fun createAlert(@Body request: CreateAlertRequest): AlertResponse
    
    @PUT("alerts/user/{user_id}/mark-all-read")
    suspend fun markAllAlertsAsRead(@Path("user_id") userId: Int): Map<String, Any>
} 