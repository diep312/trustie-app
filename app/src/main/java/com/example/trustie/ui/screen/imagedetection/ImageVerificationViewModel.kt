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
import com.example.trustie.repository.imagerepo.ImageVerificationRepositoryImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageVerificationViewModel : ViewModel() {
    private val repository: ImageVerificationRepository = ImageVerificationRepositoryImpl()
    private val globalStateManager: GlobalStateManager = GlobalStateManager()
    
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
                _uiState.value = _uiState.value.copy(
                    verificationState = response.toVerificationState(),
                    isLoading = false,
                    ocrText = response.ocrText
                )
                Log.d("ImageVerificationDebug", "Verification finished. State: ${response.toVerificationState()}, OCR: ${response.ocrText}")
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