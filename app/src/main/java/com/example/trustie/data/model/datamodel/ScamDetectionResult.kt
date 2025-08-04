package com.example.trustie.data.model.datamodel

import com.google.gson.annotations.SerializedName

data class ScamDetectionResult(
    val id: Int,
    @SerializedName("source_type")
    val sourceType: String, // "phone", "screenshot", "website", "sms"
    @SerializedName("source_id")
    val sourceId: Int,
    @SerializedName("result_label")
    val resultLabel: String, // "safe", "scam", "suspicious", "unknown"
    @SerializedName("confidence_score")
    val confidenceScore: Double, // 0.0-1.0
    @SerializedName("risk_score")
    val riskScore: Int, // 0-100
    @SerializedName("detection_method")
    val detectionMethod: String,
    @SerializedName("analysis_details")
    val analysisDetails: String? = null,
    @SerializedName("ai_model_version")
    val aiModelVersion: String,
    @SerializedName("processing_time")
    val processingTime: Double,
    @SerializedName("scan_request_id")
    val scanRequestId: Int
) 