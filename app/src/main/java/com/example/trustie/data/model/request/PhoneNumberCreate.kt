package com.example.trustie.data.model.request

import com.google.gson.annotations.SerializedName

data class PhoneNumberCreate(
    val number: String,
    @SerializedName("country_code")
    val countryCode: String? = null,
    val info: String? = null,
    val origin: String? = null,
    @SerializedName("owner_id")
    val ownerId: Int? = null
) 