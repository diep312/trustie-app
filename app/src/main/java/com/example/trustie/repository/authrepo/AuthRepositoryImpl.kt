package com.example.trustie.repository.authrepo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.trustie.data.model.datamodel.User
import com.example.trustie.repository.authrepo.AuthRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.get

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : AuthRepository {

    companion object {
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_DEVICE_ID_KEY = stringPreferencesKey("user_device_id")
        private val USER_IS_ELDERLY_KEY = booleanPreferencesKey("user_is_elderly")
        private val USER_IS_ACTIVE_KEY = booleanPreferencesKey("user_is_active")
        private val USER_CREATED_AT_KEY = stringPreferencesKey("user_created_at")
        private val USER_UPDATED_AT_KEY = stringPreferencesKey("user_updated_at")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }

    override fun getCurrentUser(): Flow<User?> {
        return context.dataStore.data.map { preferences ->
            val isLoggedIn = preferences[IS_LOGGED_IN_KEY] ?: false
            if (!isLoggedIn) {
                null
            } else {
                try {
                    User(
                        id = preferences[USER_ID_KEY] ?: 0,
                        name = preferences[USER_NAME_KEY] ?: "",
                        email = preferences[USER_EMAIL_KEY] ?: "",
                        deviceId = preferences[USER_DEVICE_ID_KEY] ?: "",
                        isElderly = preferences[USER_IS_ELDERLY_KEY] ?: false,
                        isActive = preferences[USER_IS_ACTIVE_KEY] ?: false,
                        createdAt = preferences[USER_CREATED_AT_KEY] ?: "",
                        updatedAt = preferences[USER_UPDATED_AT_KEY] ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }.first()
    }

    override suspend fun loginWithFixedUser(): User {
        val fixedUser = User(
            id = 2,
            name = "Nguyễn Văn A",
            email = "nguyenvana@example.com",
            deviceId = "SAMSUNG_SMF21",
            isElderly = true,
            isActive = true,
            createdAt = "2025-07-31 18:33:20.031",
            updatedAt = "2025-07-31 18:33:20.031"
        )
        
        saveUser(fixedUser)
        return fixedUser
    }

    override suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = false
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_NAME_KEY)
            preferences.remove(USER_EMAIL_KEY)
            preferences.remove(USER_DEVICE_ID_KEY)
            preferences.remove(USER_IS_ELDERLY_KEY)
            preferences.remove(USER_IS_ACTIVE_KEY)
            preferences.remove(USER_CREATED_AT_KEY)
            preferences.remove(USER_UPDATED_AT_KEY)
        }
    }

    override suspend fun saveUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = true
            preferences[USER_ID_KEY] = user.id
            preferences[USER_NAME_KEY] = user.name
            preferences[USER_EMAIL_KEY] = user.email ?: ""
            preferences[USER_DEVICE_ID_KEY] = user.deviceId
            preferences[USER_IS_ELDERLY_KEY] = user.isElderly
            preferences[USER_IS_ACTIVE_KEY] = user.isActive
            preferences[USER_CREATED_AT_KEY] = user.createdAt
            preferences[USER_UPDATED_AT_KEY] = user.updatedAt
        }
    }

    override suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = false
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_NAME_KEY)
            preferences.remove(USER_EMAIL_KEY)
            preferences.remove(USER_DEVICE_ID_KEY)
            preferences.remove(USER_IS_ELDERLY_KEY)
            preferences.remove(USER_IS_ACTIVE_KEY)
            preferences.remove(USER_CREATED_AT_KEY)
            preferences.remove(USER_UPDATED_AT_KEY)
        }
    }
}

