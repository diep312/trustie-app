package com.example.trustie.utils

import android.content.Context
import android.content.SharedPreferences

object UserUtils {
    private const val PREFS_NAME = "trustie_user_prefs"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_USER_EMAIL = "user_email"

    private var sharedPrefs: SharedPreferences? = null

    fun init(context: Context) {
        sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Get actual user data from SharedPreferences or return valid defaults
    fun getCurrentUserId(): Int {
        return sharedPrefs?.getInt(KEY_USER_ID, 0) ?: 0
    }

    fun getCurrentUserName(): String {
        return sharedPrefs?.getString(KEY_USER_NAME, "") ?: "Người thân"
    }

    fun getCurrentUserPhone(): String {
        return sharedPrefs?.getString(KEY_USER_PHONE, "") ?: ""
    }

    fun getCurrentUserEmail(): String {
        return sharedPrefs?.getString(KEY_USER_EMAIL, "") ?: ""
    }

    // Save user data
    fun saveUserData(userId: Int, name: String, phone: String, email: String? = null) {
        sharedPrefs?.edit()?.apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_PHONE, phone)
            putString(KEY_USER_EMAIL, email)
            apply()
        }
    }

    // Check if user data is valid
    fun isUserDataValid(): Boolean {
        return getCurrentUserId() > 0 &&
                getCurrentUserName().isNotBlank() &&
                getCurrentUserPhone().isNotBlank()
    }
}

