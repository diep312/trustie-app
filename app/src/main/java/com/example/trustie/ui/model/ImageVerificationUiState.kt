package com.example.trustie.ui.model

// Enum để định nghĩa các trạng thái của quá trình kiểm tra ảnh
enum class VerificationState {
    INITIAL, // Trạng thái ban đầu, chờ tải ảnh lên
    LOADING, // Đang kiểm tra ảnh
    WARNING, // Phát hiện nội dung lừa đảo
    SAFE     // Không phát hiện nội dung lừa đảo
}

// Data class để giữ trạng thái UI của màn hình kiểm tra ảnh
data class ImageVerificationUiState(
    val verificationState: VerificationState = VerificationState.INITIAL,
    val selectedImageUri: String? = null, // URI của ảnh đã chọn
    val isLoading: Boolean = false,      // Trạng thái tải
    val errorMessage: String? = null     // Thông báo lỗi nếu có
)
