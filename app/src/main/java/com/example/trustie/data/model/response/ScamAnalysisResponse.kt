package com.example.trustie.data.model.response

data class ScamAnalysisResponse(
    val analysis: String,
    val recommendation: String,
    val risk_level: String,
    val confidence: Int,
    val model_used: String
)