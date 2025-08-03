package com.example.trustie.data.model.request

import com.google.gson.annotations.SerializedName

data class CreateAlertRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("alert_type")
    val alertType: String,
    val severity: String = "medium",
    val message: String,
    @SerializedName("detection_result_id")
    val detectionResultId: Int,
    @SerializedName("family_member_id")
    val familyMemberId: Int? = null
) 