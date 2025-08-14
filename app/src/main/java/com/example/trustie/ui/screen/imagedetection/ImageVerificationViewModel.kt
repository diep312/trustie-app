
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
import com.example.trustie.ui.screen.scamresult.ScamResultData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageVerificationViewModel @Inject constructor(
    private val repository: ImageVerificationRepository,
    private val globalStateManager: GlobalStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImageVerificationUiState())
    val uiState: StateFlow<ImageVerificationUiState> = _uiState.asStateFlow()

    // Flag to control navigation - prevent navigation loop
    private var hasNavigated = false

    init {
        Log.d("ImageVerificationDebug", "ImageVerificationViewModel initialized")
        // Always start with clean state
        resetToInitial()
    }

    fun selectImage(uri: Uri?) {
        Log.d("ImageVerificationDebug", "selectImage called with URI: $uri")
        _uiState.value = _uiState.value.copy(
            selectedImageUri = uri?.toString(),
            verificationState = VerificationState.INITIAL,
            errorMessage = null,
            ocrText = null,
            verificationResponse = null,
            isLoading = false
        )
    }

    fun verifyImage() {
        viewModelScope.launch {
            Log.d("ImageVerificationDebug", "verifyImage called")
            val currentImageUri = _uiState.value.selectedImageUri

            if (currentImageUri.isNullOrBlank()) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Vui lòng chọn ảnh để kiểm tra."
                )
                return@launch
            }

            val userId = globalStateManager.getUserId() ?: 1
            Log.d("ImageVerificationDebug", "Starting verification with userId: $userId")

            // Set loading state
            _uiState.value = _uiState.value.copy(
                verificationState = VerificationState.LOADING,
                isLoading = true,
                errorMessage = null,
                verificationResponse = null
            )

            val request = ImageVerificationRequest(
                userId = userId,
                description = "Image verification request from app",
                imageUri = currentImageUri
            )

            try {
                Log.d("ImageVerificationDebug", "Calling repository.verifyImage...")
                val result = repository.verifyImage(request)

                result.onSuccess { response ->
                    Log.d("ImageVerificationDebug", "SUCCESS: Got response from repository")
                    Log.d("ImageVerificationDebug", "Risk Level: ${response.llmAnalysis.riskLevel}")
                    Log.d("ImageVerificationDebug", "Confidence: ${response.llmAnalysis.confidence}")

                    // Store in GlobalStateManager
                    globalStateManager.setScamResultData(
                        ScamResultData.ImageVerification(response)
                    )
                    Log.d("ImageVerificationDebug", "Stored response in GlobalStateManager")

                    // Update UI state with response - this will trigger navigation
                    _uiState.value = _uiState.value.copy(
                        verificationState = VerificationState.INITIAL, // Reset to initial
                        isLoading = false,
                        ocrText = response.ocrText,
                        verificationResponse = response, // This triggers navigation
                        errorMessage = null
                    )

                    Log.d("ImageVerificationDebug", "Updated UI state with response")

                }.onFailure { e ->
                    Log.e("ImageVerificationDebug", "FAILURE: ${e.message}", e)
                    _uiState.value = _uiState.value.copy(
                        verificationState = VerificationState.INITIAL,
                        isLoading = false,
                        errorMessage = e.message ?: "Lỗi không xác định",
                        verificationResponse = null
                    )
                }
            } catch (e: Exception) {
                Log.e("ImageVerificationDebug", "EXCEPTION: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    verificationState = VerificationState.INITIAL,
                    isLoading = false,
                    errorMessage = "Lỗi không mong muốn: ${e.message}",
                    verificationResponse = null
                )
            }
        }
    }

    fun clearResponseAfterNavigation() {
        Log.d("ImageVerificationDebug", "clearResponseAfterNavigation called")
        _uiState.value = _uiState.value.copy(
            verificationResponse = null
        )
    }

    fun resetToInitial() {
        Log.d("ImageVerificationDebug", "resetToInitial called")
        globalStateManager.clearScamResultData()
        _uiState.value = ImageVerificationUiState()
        // Reset navigation flag
        hasNavigated = false
    }

    fun showGuide() {
        Log.d("ImageVerificationDebug", "showGuide called")
        // TODO: Implement guide
    }

    fun reportFraud() {
        viewModelScope.launch {
            Log.d("ImageVerificationDebug", "reportFraud called")
            // TODO: Implement actual fraud reporting logic
            resetToInitial()
            Log.d("ImageVerificationDebug", "Fraud reported and reset to initial.")
        }
    }

    fun reportSafe() {
        viewModelScope.launch {
            Log.d("ImageVerificationDebug", "reportSafe called")
            // TODO: Implement actual safe reporting logic
            resetToInitial()
            Log.d("ImageVerificationDebug", "Safe reported and reset to initial.")
        }
    }

    // Function to check and set navigation flag
    fun shouldNavigate(): Boolean {
        return if (!hasNavigated) {
            hasNavigated = true
            true
        } else {
            false
        }
    }

    // Function to reset navigation flag when needed
    fun resetNavigationFlag() {
        hasNavigated = false
    }
}

