package com.example.trustie.data.model.response

import com.example.trustie.data.model.VerificationState
import com.google.gson.annotations.SerializedName
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import android.util.Log
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageVerificationResponse(
    @SerializedName("screenshot_id")
    val screenshotId: Int,

    @SerializedName("ocr_text")
    val ocrText: String?,

    @SerializedName("entities")
    val entities: Entities?,

    @SerializedName("llm_analysis")
    val llmAnalysis: LlmAnalysis
) : Parcelable

@Parcelize
data class Entities(
    @SerializedName("phones")
    val phones: List<String>?,

    @SerializedName("urls")
    val urls: List<String>?,

    @SerializedName("emails")
    val emails: List<String>?
) : Parcelable

@Parcelize
data class LlmAnalysis(
    @SerializedName("analysis")
    val analysis: String?,

    @SerializedName("risk_level")
    val riskLevel: String,

    @SerializedName("confidence")
    val confidence: Int?,

    @SerializedName("model_used")
    val modelUsed: String?
) : Parcelable

@Parcelize
data class ParsedAnalysis(
    @SerializedName("RISK_LEVEL")
    val riskLevel: String?,

    @SerializedName("CONFIDENCE")
    val confidence: Int?,

    @SerializedName("ANALYSIS")
    val analysisText: String?,

    @SerializedName("RECOMMENDATIONS")
    val recommendations: List<String>?
) : Parcelable

// Extension function to parse the analysis JSON
fun LlmAnalysis.getParsedAnalysis(): ParsedAnalysis? {
    return try {
        Log.d("ParsedAnalysis", "Raw analysis: '$analysis'")

        if (analysis.isNullOrBlank()) {
            Log.d("ParsedAnalysis", "Analysis is null or blank")
            return null
        }

        val trimmedAnalysis = analysis.trim()

        val result = when {
            trimmedAnalysis.startsWith("```json") && trimmedAnalysis.endsWith("```") -> {
                val jsonString = trimmedAnalysis.removePrefix("```json").removeSuffix("```").trim()
                Log.d("ParsedAnalysis", "Parsing markdown JSON: '$jsonString'")
                Gson().fromJson(jsonString, ParsedAnalysis::class.java)
            }
            trimmedAnalysis.startsWith("{") && trimmedAnalysis.endsWith("}") -> {
                Log.d("ParsedAnalysis", "Parsing direct JSON: '$trimmedAnalysis'")
                Gson().fromJson(trimmedAnalysis, ParsedAnalysis::class.java)
            }
            else -> {
                Log.d("ParsedAnalysis", "Analysis is not JSON format")
                null
            }
        }

        Log.d("ParsedAnalysis", "Parsed result: $result")
        result

    } catch (e: Exception) {
        Log.e("ParsedAnalysis", "Error parsing analysis: ${e.message}", e)
        null
    }
}

// Extension function to get the effective confidence - SIMPLIFIED
fun ImageVerificationResponse.getEffectiveConfidence(): Int? {
    Log.d("EffectiveConfidence", "=== CONFIDENCE DEBUG START ===")
    Log.d("EffectiveConfidence", "Main confidence from API: ${llmAnalysis.confidence}")

    // Try to parse JSON first
    val parsedAnalysis = llmAnalysis.getParsedAnalysis()
    val parsedConfidence = parsedAnalysis?.confidence
    Log.d("EffectiveConfidence", "Parsed confidence from JSON: $parsedConfidence")

    // Priority: parsed confidence > main confidence
    val finalConfidence = parsedConfidence ?: llmAnalysis.confidence
    Log.d("EffectiveConfidence", "Final confidence: $finalConfidence")
    Log.d("EffectiveConfidence", "=== CONFIDENCE DEBUG END ===")

    return finalConfidence
}

// Extension function to get the effective risk level
fun ImageVerificationResponse.getEffectiveRiskLevel(): String {
    val parsedAnalysis = llmAnalysis.getParsedAnalysis()
    val analysisRiskLevel = parsedAnalysis?.riskLevel

    Log.d("EffectiveRiskLevel", "Parsed risk level: $analysisRiskLevel")
    Log.d("EffectiveRiskLevel", "Main risk level: ${llmAnalysis.riskLevel}")

    return analysisRiskLevel ?: llmAnalysis.riskLevel
}

// Extension function to convert response to VerificationState
fun ImageVerificationResponse.toVerificationState(): VerificationState {
    val effectiveRiskLevel = getEffectiveRiskLevel()

    return when (effectiveRiskLevel.uppercase()) {
        "HIGH" -> VerificationState.WARNING
        "MEDIUM" -> VerificationState.WARNING
        "LOW" -> VerificationState.SAFE
        else -> VerificationState.SAFE
    }
}

// Extension function to get readable analysis text
fun ImageVerificationResponse.getReadableAnalysis(): String {
    val parsedAnalysis = llmAnalysis.getParsedAnalysis()
    return parsedAnalysis?.analysisText ?: llmAnalysis.analysis ?: "Không có thông tin phân tích"
}

// Extension function to get recommendations
fun ImageVerificationResponse.getRecommendations(): List<String> {
    val parsedAnalysis = llmAnalysis.getParsedAnalysis()
    return parsedAnalysis?.recommendations ?: emptyList()
}

