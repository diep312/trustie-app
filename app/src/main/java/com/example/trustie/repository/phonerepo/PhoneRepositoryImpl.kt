package com.example.trustie.repository.phonerepo

import com.example.trustie.data.api.ApiManager
import com.example.trustie.data.model.request.PhoneCheckRequest
import com.example.trustie.data.model.request.PhoneNumberCreate
import com.example.trustie.data.model.request.PhoneSearchRequest
import com.example.trustie.data.model.response.PhoneCheckResponse
import com.example.trustie.data.model.response.PhoneNumber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhoneRepositoryImpl @Inject constructor() : PhoneRepository {

    private val phoneApi = ApiManager.phoneApi

    override suspend fun checkPhoneNumber(phoneNumber: String, userId: Int?): PhoneCheckResponse {
        return phoneApi.checkPhoneNumber(
            PhoneCheckRequest(
                phoneNumber = phoneNumber,
                userId = userId
            )
        )
    }

    override suspend fun getFlaggedPhones(limit: Int, offset: Int): List<PhoneNumber> {
        return phoneApi.getFlaggedPhones(limit = limit, offset = offset)
    }

    override suspend fun searchPhones(query: String, limit: Int): List<PhoneNumber> {
        return phoneApi.searchPhones(
            PhoneSearchRequest(
                query = query,
                limit = limit
            )
        )
    }

    override suspend fun getUserPhones(userId: Int): List<PhoneNumber> {
        return phoneApi.getUserPhones(userId)
    }

    override suspend fun getPhoneById(phoneId: Int): PhoneNumber {
        return phoneApi.getPhoneById(phoneId)
    }

    override suspend fun addPhoneNumber(
        number: String,
        countryCode: String?,
        info: String?,
        origin: String?,
        ownerId: Int?
    ): PhoneNumber {
        return phoneApi.addPhoneNumber(
            PhoneNumberCreate(
                number = number,
                countryCode = countryCode,
                info = info,
                origin = origin,
                ownerId = ownerId
            )
        )
    }
}