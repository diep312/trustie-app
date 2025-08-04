package com.example.trustie.data.api

import com.example.trustie.data.model.request.LinkRequest
import retrofit2.http.*

interface FamilyApiService {
    
    @POST("family/link-family")
    suspend fun linkFamily(@Body request: LinkRequest): Map<String, Any>
    
    @GET("family/link-status/{elderly_user_id}/{family_user_id}")
    suspend fun checkLinkStatus(
        @Path("elderly_user_id") elderlyUserId: Int,
        @Path("family_user_id") familyUserId: Int
    ): Map<String, Any>
    
    @DELETE("family/unlink-family/{elderly_user_id}/{family_user_id}")
    suspend fun unlinkFamilyMember(
        @Path("elderly_user_id") elderlyUserId: Int,
        @Path("family_user_id") familyUserId: Int
    ): Map<String, Any>
} 