package com.example.trustie.data.api

import com.example.trustie.ui.model.CallHistoryResponse
import com.example.trustie.ui.model.ConnectionResponse
import com.example.trustie.ui.model.PhoneCheckResponse
import kotlinx.coroutines.delay

class ApiService {


    private val baseUrl = "https://your-api-domain.com/api"

    suspend fun getCallHistory(): CallHistoryResponse {

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
class PhoneCheckApiService {
    // Base URL for your backend API
    private val baseUrl = "https://your-api-domain.com/api/"

    suspend fun checkPhoneNumber(phoneNumber: String): PhoneCheckResponse {
        // Simulate API call
        delay(1000)
        throw NotImplementedError("Implement actual API call here")
    }
}


class ConnectionApiService {
    // Base URL for your backend API
    private val baseUrl = "https://your-api-domain.com/api/"

    suspend fun generateQRCode(): ConnectionResponse {
        // Simulate API call
        delay(1000)
        throw NotImplementedError("Implement actual API call here")
    }

    suspend fun getConnections(): ConnectionResponse {
        // Simulate API call
        delay(1000)
        throw NotImplementedError("Implement actual API call here")
    }

    suspend fun removeConnection(connectionId: String): Boolean {
        // Simulate API call
        delay(500)
        throw NotImplementedError("Implement actual API call here")
    }
}


