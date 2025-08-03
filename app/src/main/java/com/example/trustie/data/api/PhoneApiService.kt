package com.example.trustie.data.api

import com.example.trustie.data.model.request.FlagPhoneRequest
import com.example.trustie.data.model.request.PhoneCheckRequest
import com.example.trustie.data.model.request.PhoneNumberCreate
import com.example.trustie.data.model.request.PhoneSearchRequest
import com.example.trustie.data.model.response.PhoneCheckResponse
import com.example.trustie.data.model.response.PhoneNumber
import retrofit2.http.*

interface PhoneApiService {
    
    @POST("phone/check")
    suspend fun checkPhoneNumber(@Body request: PhoneCheckRequest): PhoneCheckResponse
    
//    @POST("phone/flag")
//    suspend fun flagPhoneNumber(@Body request: FlagPhoneRequest): PhoneNumber
    
    @POST("phone/add")
    suspend fun addPhoneNumber(@Body request: PhoneNumberCreate): PhoneNumber
    
    @GET("phone/flagged")
    suspend fun getFlaggedPhones(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): List<PhoneNumber>
    
    @GET("phone/{phone_id}")
    suspend fun getPhoneById(@Path("phone_id") phoneId: Int): PhoneNumber
    
//    @PUT("phone/{phone_id}/risk-score")
//    suspend fun updatePhoneRiskScore(
//        @Path("phone_id") phoneId: Int,
//        @Query("risk_score") riskScore: Int
//    ): PhoneNumber
    
    @POST("phone/search")
    suspend fun searchPhones(@Body request: PhoneSearchRequest): List<PhoneNumber>
    
    @GET("phone/user/{user_id}")
    suspend fun getUserPhones(@Path("user_id") userId: Int): List<PhoneNumber>
    
    @POST("phone/check-and-alert")
    suspend fun checkPhoneAndCreateAlert(@Body request: PhoneCheckRequest): Map<String, Any>
} 