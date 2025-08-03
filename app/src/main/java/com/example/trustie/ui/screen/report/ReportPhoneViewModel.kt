package com.example.trustie.ui.screen.report

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.repository.reportrepo.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportPhoneViewModel(
    private val repository: ReportRepository = ReportRepository()
) : ViewModel() {

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _selectedReason = MutableStateFlow<String?>(null)
    val selectedReason: StateFlow<String?> = _selectedReason.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    val reportReasons = listOf(
        "Lừa đảo",
        "Làm phiền",
        "Quảng cáo",
        "Giả danh CQCN",
        "Lí do khác"
    )

    init {
        Log.d("ReportVMDebug", "ReportPhoneViewModel initialized")
    }

    fun updatePhoneNumber(phone: String) {
        _phoneNumber.value = phone
        clearMessages() // Xóa thông báo khi người dùng thay đổi input
        Log.d("ReportVMDebug", "Phone number updated: $phone")
    }

    fun updateSelectedReason(reason: String) {
        _selectedReason.value = reason
        clearMessages() // Xóa thông báo khi người dùng thay đổi input
        Log.d("ReportVMDebug", "Selected reason updated: $reason")
    }

    fun submitReport() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            if (_phoneNumber.value.isBlank()) {
                _errorMessage.value = "Vui lòng nhập số điện thoại."
                _isLoading.value = false
                return@launch
            }
            if (_selectedReason.value == null) {
                _errorMessage.value = "Vui lòng chọn lí do báo cáo."
                _isLoading.value = false
                return@launch
            }

            try {
                Log.d("ReportVMDebug", "Submitting report...")
                val response = repository.submitPhoneReport(
                    _phoneNumber.value,
                    _selectedReason.value!!
                )

                if (response.success) {
                    _successMessage.value = response.message ?: "Báo cáo thành công!"
                    // Reset form sau khi báo cáo thành công
                    _phoneNumber.value = ""
                    _selectedReason.value = null
                    Log.d("ReportVMDebug", "Report submitted successfully")
                } else {
                    _errorMessage.value = response.message ?: "Không thể gửi báo cáo."
                    Log.e("ReportVMDebug", "Failed to submit report: ${response.message}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
                Log.e("ReportVMDebug", "Exception submitting report: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}