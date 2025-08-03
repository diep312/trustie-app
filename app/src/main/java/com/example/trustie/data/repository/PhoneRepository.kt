package com.example.trustie.data.repository

import com.example.trustie.data.api.ApiManager
import com.example.trustie.data.model.request.PhoneCheckRequest
import com.example.trustie.data.model.response.PhoneCheckResponse
import com.example.trustie.data.model.response.PhoneNumber

class PhoneRepository {
    private val phoneApi = ApiManager.phoneApi
    
    suspend fun checkPhoneNumber(phoneNumber: String, userId: Int? = null): PhoneCheckResponse {
        return phoneApi.checkPhoneNumber(
            PhoneCheckRequest(
                phoneNumber = phoneNumber,
                userId = userId
            )
        )
    }
    
    suspend fun getFlaggedPhones(limit: Int = 100, offset: Int = 0): List<PhoneNumber> {
        return phoneApi.getFlaggedPhones(limit = limit, offset = offset)
    }
    
    suspend fun searchPhones(query: String, limit: Int = 50): List<PhoneNumber> {
        return phoneApi.searchPhones(
            com.example.trustie.data.model.request.PhoneSearchRequest(
                query = query,
                limit = limit
            )
        )
    }
    
    suspend fun getUserPhones(userId: Int): List<PhoneNumber> {
        return phoneApi.getUserPhones(userId)
    }
} 