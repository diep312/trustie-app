package com.example.trustie.ui.viewmodel

import android.util.Log // Import Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.ui.model.ImageVerificationUiState
import com.example.trustie.ui.model.VerificationState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageVerificationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ImageVerificationUiState())
    val uiState: StateFlow<ImageVerificationUiState> = _uiState.asStateFlow()

    init {
        Log.d("ImageVerificationDebug", "ImageVerificationViewModel initialized")
    }

    fun selectImage() {
        Log.d("ImageVerificationDebug", "selectImage called")
        // Trong ứng dụng thực tế, hàm này sẽ kích hoạt trình chọn ảnh
        // Tạm thời, chúng ta chỉ mô phỏng việc chọn ảnh
        _uiState.value = _uiState.value.copy(
            selectedImageUri = "selected_image_uri_placeholder" // Thay thế bằng URI thực tế
        )
    }

    fun verifyImage() {
        viewModelScope.launch {
            Log.d("ImageVerificationDebug", "verifyImage called")
            _uiState.value = _uiState.value.copy(
                verificationState = VerificationState.LOADING,
                isLoading = true,
                errorMessage = null
            )

            // Mô phỏng độ trễ của cuộc gọi API
            delay(2000) // Đợi 2 giây

            // Mô phỏng kết quả ngẫu nhiên cho mục đích demo
            val isScam = (0..1).random() == 0 // 50% là lừa đảo, 50% là an toàn

            _uiState.value = _uiState.value.copy(
                verificationState = if (isScam) VerificationState.WARNING else VerificationState.SAFE,
                isLoading = false
            )
            Log.d("ImageVerificationDebug", "Verification finished. State: ${_uiState.value.verificationState}")
        }
    }

    fun showGuide() {
        Log.d("ImageVerificationDebug", "showGuide called")
        // Xử lý chức năng hướng dẫn
        // Có thể điều hướng đến màn hình hướng dẫn hoặc hiển thị một dialog
    }

    fun resetToInitial() {
        Log.d("ImageVerificationDebug", "resetToInitial called")
        _uiState.value = ImageVerificationUiState()
    }

    fun reportFraud() {
        viewModelScope.launch {
            Log.d("ImageVerificationDebug", "reportFraud called")
            // Mô phỏng cuộc gọi API báo cáo
            delay(1000)
            // Có thể hiển thị thông báo thành công hoặc điều hướng lại
            resetToInitial() // Quay về trạng thái ban đầu sau khi báo cáo
            Log.d("ImageVerificationDebug", "Fraud reported and reset to initial.")
        }
    }

    fun reportSafe() {
        viewModelScope.launch {
            Log.d("ImageVerificationDebug", "reportSafe called")
            // Mô phỏng cuộc gọi API báo cáo
            delay(1000)
            // Có thể hiển thị thông báo thành công hoặc điều hướng lại
            resetToInitial() // Quay về trạng thái ban đầu sau khi báo cáo
            Log.d("ImageVerificationDebug", "Safe reported and reset to initial.")
        }
    }
}
