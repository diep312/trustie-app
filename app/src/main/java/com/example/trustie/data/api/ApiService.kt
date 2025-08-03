package com.example.trustie.data.api

import com.example.trustie.data.model.CallHistoryResponse
import com.example.trustie.data.model.ConnectionResponse
import com.example.trustie.data.model.PhoneCheckResponse
import kotlinx.coroutines.delay

// Lưu ý: Các class này hiện đang mô phỏng API calls.
// Nếu bạn muốn chúng hoạt động với Retrofit, chúng cần được chuyển thành interface
// và được cung cấp qua Hilt/Retrofit tương tự như AuthApiService.

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
    private val baseUrl = "https://your-api-domain.com/api/"

    suspend fun checkPhoneNumber(phoneNumber: String): PhoneCheckResponse {
        delay(1000)
        throw NotImplementedError("Implement actual API call here")
    }
}

class ConnectionApiService {
    private val baseUrl = "https://your-api-domain.com/api/"

    suspend fun generateQRCode(): ConnectionResponse {
        delay(1000)
        throw NotImplementedError("Implement actual API call here")
    }

    suspend fun getConnections(): ConnectionResponse {
        delay(1000)
        throw NotImplementedError("Implement actual API call here")
    }

    suspend fun removeConnection(connectionId: String): Boolean {
        delay(500)
        throw NotImplementedError("Implement actual API call here")
    }
}
