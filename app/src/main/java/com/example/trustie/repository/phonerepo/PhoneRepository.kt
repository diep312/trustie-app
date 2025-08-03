package com.example.trustie.repository.phonerepo

import com.example.trustie.data.model.response.PhoneCheckResponse
import com.example.trustie.data.model.response.PhoneNumber

interface PhoneRepository {
    /**
     * Check if a phone number is flagged
     */
    suspend fun checkPhoneNumber(phoneNumber: String, userId: Int? = null): PhoneCheckResponse

    /**
     * Get flagged phone numbers
     */
    suspend fun getFlaggedPhones(limit: Int = 100, offset: Int = 0): List<PhoneNumber>

    /**
     * Search phone numbers
     */
    suspend fun searchPhones(query: String, limit: Int = 50): List<PhoneNumber>

    /**
     * Get user's phone numbers
     */
    suspend fun getUserPhones(userId: Int): List<PhoneNumber>

    /**
     * Get phone number by ID
     */
    suspend fun getPhoneById(phoneId: Int): PhoneNumber

    /**
     * Add a new phone number
     */
    suspend fun addPhoneNumber(
        number: String,
        countryCode: String? = null,
        info: String? = null,
        origin: String? = null,
        ownerId: Int? = null
    ): PhoneNumber
}