package com.example.trustie.ui.screen.imagedetection

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.GlobalStateManager
import com.example.trustie.data.model.ImageVerificationUiState
import com.example.trustie.data.model.VerificationState
import com.example.trustie.data.model.request.ImageVerificationRequest
import com.example.trustie.repository.imagerepo.ImageVerificationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageVerificationViewModel @Inject constructor(
    private val repository: ImageVerificationRepository,
    private val globalStateManager: GlobalStateManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ImageVerificationUiState())
    val uiState: StateFlow<ImageVerificationUiState> = _uiState.asStateFlow()

    init {
        Log.d("ImageVerificationDebug", "ImageVerificationViewModel initialized")
    }

    fun selectImage(uri: Uri?) {
        Log.d("ImageVerificationDebug", "selectImage called with URI: $uri")
        _uiState.value = _uiState.value.copy(
            selectedImageUri = uri?.toString(),
            verificationState = VerificationState.INITIAL,
            errorMessage = null,
            ocrText = null
        )
    }

    fun verifyImage() {
        viewModelScope.launch {
            Log.d("ImageVerificationDebug", "verifyImage called")
            val currentImageUri = _uiState.value.selectedImageUri
            if (currentImageUri == null) {
                _uiState.value = _uiState.value.copy(errorMessage = "Vui lòng chọn ảnh để kiểm tra.")
                return@launch
            }

            // Get user ID from global state
            val userId = globalStateManager.getUserId() ?: 1
            Log.d("ImageVerificationDebug", "Using userId: $userId for image verification.")

            _uiState.value = _uiState.value.copy(
                verificationState = VerificationState.LOADING,
                isLoading = true,
                errorMessage = null,
                ocrText = null
            )

            // Create request object
            val request = ImageVerificationRequest(
                userId = userId,
                description = "Image verification request",
                imageUri = currentImageUri
            )

            val result = repository.verifyImage(request)
            result.onSuccess { response ->
                Log.d("ImageVerificationDebug", "Verification response received: $response")
                
                // Store the response in GlobalStateManager for ScamResultScreen
                globalStateManager.setVerificationResponse(response)
                
                // For now, we'll keep the old states for backward compatibility
                // The actual navigation to ScamResultScreen will be handled in the UI
                val nextState = when (response.llmAnalysis.riskLevel.uppercase()) {
                    "HIGH" -> VerificationState.WARNING
                    "MEDIUM" -> VerificationState.WARNING
                    "LOW" -> VerificationState.SAFE
                    else -> VerificationState.SAFE
                }
                
                _uiState.value = _uiState.value.copy(
                    verificationState = nextState,
                    isLoading = false,
                    ocrText = response.ocrText,
                    verificationResponse = response
                )
                Log.d("ImageVerificationDebug", "Verification finished. State: $nextState, Risk Level: ${response.llmAnalysis.riskLevel}")
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    verificationState = VerificationState.INITIAL,
                    isLoading = false,
                    errorMessage = "Lỗi kết nối: ${e.message}"
                )
                Log.e("ImageVerificationDebug", "Verification failed: ${e.message}", e)
            }
        }
    }

    fun showGuide() {
        Log.d("ImageVerificationDebug", "showGuide called")
    }

    fun resetToInitial() {
        Log.d("ImageVerificationDebug", "resetToInitial called")
        globalStateManager.clearVerificationResponse()
        _uiState.value = ImageVerificationUiState()
    }

    fun reportFraud() {
        viewModelScope.launch {
            Log.d("ImageVerificationDebug", "reportFraud called")
            delay(1000)
            resetToInitial()
            Log.d("ImageVerificationDebug", "Fraud reported and reset to initial.")
        }
    }

    fun reportSafe() {
        viewModelScope.launch {
            Log.d("ImageVerificationDebug", "reportSafe called")
            delay(1000)
            resetToInitial()
            Log.d("ImageVerificationDebug", "Safe reported and reset to initial.")
        }
    }
}