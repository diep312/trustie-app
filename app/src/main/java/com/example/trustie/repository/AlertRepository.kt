package com.example.trustie.repository

import com.example.trustie.data.api.ApiManager
import com.example.trustie.data.model.enums.AlertType
import com.example.trustie.data.model.enums.Severity
import com.example.trustie.data.model.request.CreateAlertRequest
import com.example.trustie.data.model.response.AlertResponse

class AlertRepository {
    private val alertApi = ApiManager.alertApi
    
    suspend fun getUserAlerts(
        userId: Int,
        limit: Int = 50,
        offset: Int = 0,
        unreadOnly: Boolean = false
    ): List<AlertResponse> {
        return alertApi.getUserAlerts(
            userId = userId,
            limit = limit,
            offset = offset,
            unreadOnly = unreadOnly
        )
    }
    
    suspend fun getUnreadAlertCount(userId: Int): Int {
        val response = alertApi.getUnreadAlertCount(userId)
        return response["count"] as? Int ?: 0
    }
    
    suspend fun markAlertAsRead(alertId: Int, userId: Int) {
        alertApi.markAlertAsRead(alertId, userId)
    }
    
    suspend fun acknowledgeAlert(alertId: Int, userId: Int) {
        alertApi.acknowledgeAlert(alertId, userId)
    }
    
    suspend fun deleteAlert(alertId: Int, userId: Int) {
        alertApi.deleteAlert(alertId, userId)
    }
    
    suspend fun getAlertsBySeverity(userId: Int, severity: Severity): List<AlertResponse> {
        return alertApi.getAlertsBySeverity(userId, severity.value)
    }
    
    suspend fun getCriticalAlerts(userId: Int): List<AlertResponse> {
        return alertApi.getCriticalAlerts(userId)
    }
    
    suspend fun createAlert(
        userId: Int,
        alertType: AlertType,
        message: String,
        detectionResultId: Int,
        severity: Severity = Severity.MEDIUM,
        familyMemberId: Int? = null
    ): AlertResponse {
        return alertApi.createAlert(
            CreateAlertRequest(
                userId = userId,
                alertType = alertType.value,
                severity = severity.value,
                message = message,
                detectionResultId = detectionResultId,
                familyMemberId = familyMemberId
            )
        )
    }
    
    suspend fun markAllAlertsAsRead(userId: Int) {
        alertApi.markAllAlertsAsRead(userId)
    }
} 