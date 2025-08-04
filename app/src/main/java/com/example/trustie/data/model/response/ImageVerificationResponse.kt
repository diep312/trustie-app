package com.example.trustie.data.model.response

import com.example.trustie.data.model.VerificationState
import com.google.gson.annotations.SerializedName

data class ImageVerificationResponse(
    @SerializedName("screenshot_id")
    val screenshotId: Int,
    @SerializedName("ocr_text")
    val ocrText: String,
    val entities: Entities,
    @SerializedName("llm_analysis")
    val llmAnalysis: LlmAnalysis
) {
    fun toVerificationState(): VerificationState {
        return when (llmAnalysis.riskLevel.uppercase()) {
            "HIGH" -> VerificationState.WARNING
            "MEDIUM" -> VerificationState.WARNING
            "LOW" -> VerificationState.SAFE
            else -> VerificationState.SAFE
        }
    }
}

data class Entities(
    val phones: List<String> = emptyList(),
    val urls: List<String> = emptyList(),
    val emails: List<String> = emptyList()
)

data class LlmAnalysis(
    val analysis: String,
    @SerializedName("risk_level")
    val riskLevel: String,
    val confidence: Int,
    @SerializedName("model_used")
    val modelUsed: String
) 