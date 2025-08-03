package com.example.trustie.data.remote.dto

import com.example.trustie.data.model.VerificationState
import com.google.gson.annotations.SerializedName

data class ImageVerificationResponse(
    @SerializedName("screenshot_id")
    val screenshotId: Int,
    @SerializedName("ocr_text")
    val ocrText: String?,
    @SerializedName("entities")
    val entities: Entities?,
    @SerializedName("llm_analysis")
    val llmAnalysis: LlmAnalysis?
) {
    fun toVerificationState(): VerificationState {
        return when (llmAnalysis?.riskLevel?.lowercase()) {
            "high", "medium" -> VerificationState.WARNING
            "low", "none" -> VerificationState.SAFE
            else -> VerificationState.INITIAL
        }
    }
}

data class Entities(
    @SerializedName("phones")
    val phones: List<String>?,
    @SerializedName("urls")
    val urls: List<String>?,
    @SerializedName("emails")
    val emails: List<String>?
)

data class LlmAnalysis(
    @SerializedName("analysis")
    val analysis: String?,
    @SerializedName("risk_level")
    val riskLevel: String?,
    @SerializedName("confidence")
    val confidence: Int?,
    @SerializedName("model_used")
    val modelUsed: String?
)