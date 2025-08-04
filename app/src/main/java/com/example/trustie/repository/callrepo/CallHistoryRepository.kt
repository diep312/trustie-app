package com.example.trustie.repository.callrepo

import com.example.trustie.data.model.datamodel.CallHistoryItem
import com.example.trustie.data.model.response.CallHistoryResponse
import kotlinx.coroutines.delay

class CallHistoryRepository {

    suspend fun getCallHistory(): CallHistoryResponse {
        return try {

            delay(1000)


            val mockData = listOf(
                CallHistoryItem(
                    id = "1",
                    contactName = "Nguyen A",
                    phoneNumber = "09 123 456 78",
                    time = "18:12",
                    isSuspicious = false
                ),
                CallHistoryItem(
                    id = "2",
                    contactName = "",
                    phoneNumber = "09 123 423 43",
                    time = "18:11",
                    isSuspicious = true
                ),
                CallHistoryItem(
                    id = "3",
                    contactName = "Ông B",
                    phoneNumber = "09 123 456 23",
                    time = "18:00",
                    isSuspicious = false
                ),
                CallHistoryItem(
                    id = "4",
                    contactName = "",
                    phoneNumber = "09 123 423 43",
                    time = "17:12",
                    isSuspicious = true
                ),
                CallHistoryItem(
                    id = "5",
                    contactName = "Hoàn",
                    phoneNumber = "03 561 415 95",
                    time = "16:00",
                    isSuspicious = false
                )
            )

            CallHistoryResponse(
                success = true,
                data = mockData
            )
        } catch (e: Exception) {
            CallHistoryResponse(
                success = false,
                data = emptyList(),
                message = e.message
            )
        }
    }
}