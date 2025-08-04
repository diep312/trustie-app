package com.example.trustie.data.model

import com.example.trustie.data.model.response.ImageVerificationResponse

enum class VerificationState {
    INITIAL,
    LOADING,
    WARNING,
    SAFE,
    SCAM_RESULT
}

data class ImageVerificationUiState(
    val verificationState: VerificationState = VerificationState.INITIAL,
    val selectedImageUri: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val ocrText: String? = null,
    val verificationResponse: ImageVerificationResponse? = null
)
