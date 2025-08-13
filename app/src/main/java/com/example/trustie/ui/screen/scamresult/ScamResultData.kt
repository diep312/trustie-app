package com.example.trustie.ui.screen.scamresult

import com.example.trustie.data.model.response.ImageVerificationResponse
import com.example.trustie.data.model.response.ScamAnalysisResponse

sealed class ScamResultData {
    data class ImageVerification(val data: ImageVerificationResponse) : ScamResultData()
    data class ScamAnalysis(val data: ScamAnalysisResponse) : ScamResultData()
}