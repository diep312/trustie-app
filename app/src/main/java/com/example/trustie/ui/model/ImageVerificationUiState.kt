package com.example.trustie.ui.model

enum class VerificationState {
    INITIAL,
    LOADING,
    WARNING,
    SAFE
}

data class ImageVerificationUiState(
    val verificationState: VerificationState = VerificationState.INITIAL,
    val selectedImageUri: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val ocrText: String? = null
)
