package com.example.trustie.data.model.request

import com.google.gson.annotations.SerializedName

data class PhoneSearchRequest(
    val query: String,
    val limit: Int = 50
) 