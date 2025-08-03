package com.example.trustie.data.model.datamodel

import com.google.gson.annotations.SerializedName

data class FamilyMember(
    val id: Int,
    val name: String,
    val relationship: String, // "spouse", "child", "parent", "sibling"
    @SerializedName("phone_number")
    val phoneNumber: String,
    val email: String? = null,
    @SerializedName("notify_on_alert")
    val notifyOnAlert: Boolean = true,
    @SerializedName("is_primary_contact")
    val isPrimaryContact: Boolean = false,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("linked_user_id")
    val linkedUserId: Int? = null
) 