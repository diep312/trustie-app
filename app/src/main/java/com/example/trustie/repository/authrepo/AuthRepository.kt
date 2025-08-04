package com.example.trustie.repository.authrepo

import com.example.trustie.data.model.datamodel.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun getCurrentUser(): Flow<User?>
    

    suspend fun isLoggedIn(): Boolean


    suspend fun loginWithFixedUser(): User


    suspend fun logout()


    suspend fun saveUser(user: User)


    suspend fun clearUser()
}