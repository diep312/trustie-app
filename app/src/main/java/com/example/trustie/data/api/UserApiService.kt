package com.example.trustie.data.api

import com.example.trustie.data.model.request.UserCreate
import com.example.trustie.data.model.response.User
import retrofit2.http.*

interface UserApiService {
    
    @POST("users/")
    suspend fun createUser(@Body request: UserCreate): User
    
//    @GET("users/")
//    suspend fun getUsers(
//        @Query("skip") skip: Int = 0,
//        @Query("limit") limit: Int = 100
//    ): List<User>
    
    @GET("users/{user_id}")
    suspend fun getUser(@Path("user_id") userId: Int): User
    
    @PUT("users/{user_id}")
    suspend fun updateUser(
        @Path("user_id") userId: Int,
        @Body request: UserCreate
    ): User
    
    @DELETE("users/{user_id}")
    suspend fun deleteUser(@Path("user_id") userId: Int): Map<String, Any>
} 