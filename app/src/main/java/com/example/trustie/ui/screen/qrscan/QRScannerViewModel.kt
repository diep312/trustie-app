//package com.example.trustie.ui.screen.qrscanner
//
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.trustie.data.model.LinkFamilyResponse
//import com.example.trustie.data.model.QRScanResult
//import com.example.trustie.data.model.request.LinkRequest
//import com.example.trustie.repository.connectrepo.ConnectionRepository
//import com.example.trustie.utils.UserUtils
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class QRScannerViewModel @Inject constructor(
//    private val repository: ConnectionRepository
//) : ViewModel() {
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
//
//    private val _errorMessage = MutableStateFlow<String?>(null)
//    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
//
//    private val _scanResult = MutableStateFlow<LinkFamilyResponse?>(null)
//    val scanResult: StateFlow<LinkFamilyResponse?> = _scanResult.asStateFlow()
//
//    private var hasProcessedQR = false
//
//    fun processQRCode(qrData: String) {
//        if (hasProcessedQR || _isLoading.value) {
//            return // Prevent multiple processing
//        }
//
//        hasProcessedQR = true
//
//        viewModelScope.launch {
//            _isLoading.value = true
//            _errorMessage.value = null
//
//            try {
//                Log.d("QRScannerViewModel", "Processing QR code: $qrData")
//
//                // Parse QR code
//                val parseResult = repository.parseQRCode(qrData)
//
//                if (!parseResult.success || parseResult.elderlyUserId == null) {
//                    _errorMessage.value = parseResult.message ?: "Mã QR không hợp lệ"
//                    hasProcessedQR = false
//                    return@launch
//                }
//
//                // Get current user info
//                val currentUserId = UserUtils.getCurrentUserId()
//                val currentUserName = UserUtils.getCurrentUserName()
//                val currentUserPhone = UserUtils.getCurrentUserPhone()
//                val currentUserEmail = UserUtils.getCurrentUserEmail()
//
//                // Create link request
//                val linkRequest = LinkRequest(
//                    scannedPayload = qrData,
//                    familyUserId = currentUserId,
//                    name = currentUserName,
//                    relationship = "Người thân", // Default relationship
//                    phoneNumber = null,
//                    email = currentUserEmail
//                )
//
//                // Make API call to link family
//                val linkResult = repository.linkFamily(linkRequest)
//
//                if (linkResult.success) {
//                    _scanResult.value = linkResult
//                    Log.d("QRScannerViewModel", "Family linked successfully")
//                } else {
//                    _errorMessage.value = linkResult.message ?: "Không thể kết nối"
//                    hasProcessedQR = false
//                }
//
//            } catch (e: Exception) {
//                Log.e("QRScannerViewModel", "Error processing QR code: ${e.message}", e)
//                _errorMessage.value = "Lỗi xử lý: ${e.message}"
//                hasProcessedQR = false
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//
//    fun clearError() {
//        _errorMessage.value = null
//        hasProcessedQR = false
//    }
//
//    fun resetScanResult() {
//        _scanResult.value = null
//        hasProcessedQR = false
//    }
//}

package com.example.trustie.ui.screen.qrscanner

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.model.LinkFamilyResponse
import com.example.trustie.data.model.request.LinkRequest
import com.example.trustie.repository.connectrepo.ConnectionRepository
import com.example.trustie.utils.UserUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QRScannerViewModel @Inject constructor(
    private val repository: ConnectionRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _scanResult = MutableStateFlow<LinkFamilyResponse?>(null)
    val scanResult: StateFlow<LinkFamilyResponse?> = _scanResult.asStateFlow()

    private var hasProcessedQR = false

    fun processQRCode(qrData: String) {
        if (hasProcessedQR || _isLoading.value) {
            return // Prevent multiple processing
        }

        hasProcessedQR = true

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("QRScannerViewModel", "Processing QR code: $qrData")

                // Parse QR code
                val parseResult = repository.parseQRCode(qrData)

                if (!parseResult.success) {
                    _errorMessage.value = parseResult.message ?: "Mã QR không hợp lệ"
                    hasProcessedQR = false
                    return@launch
                }

                // Get current user info
                val currentUserId = UserUtils.getCurrentUserId()
                val currentUserName = UserUtils.getCurrentUserName()
                val currentUserPhone = UserUtils.getCurrentUserPhone()
                val currentUserEmail = UserUtils.getCurrentUserEmail()

                Log.d("QRScannerViewModel", "Current user ID: $currentUserId")
                Log.d("QRScannerViewModel", "Current user name: $currentUserName")
                Log.d("QRScannerViewModel", "Current user phone: $currentUserPhone")
                Log.d("QRScannerViewModel", "Current user email: $currentUserEmail")

                // Validate user data
                if (currentUserId <= 0) {
                    _errorMessage.value = "ID người dùng không hợp lệ - User chưa đăng nhập"
                    hasProcessedQR = false
                    return@launch
                }

                if (currentUserName.isBlank()) {
                    _errorMessage.value = "Tên người dùng không hợp lệ"
                    hasProcessedQR = false
                    return@launch
                }

                // Phone number is optional now

                // Create link request with proper data
                val linkRequest = LinkRequest(
                    scannedPayload = qrData,
                    familyUserId = currentUserId,
                    name = currentUserName,
                    relationship = "Người thân",
                    phoneNumber = currentUserPhone.takeIf { it.isNotBlank() }, // Make phone optional
                    email = currentUserEmail.takeIf { it.isNotBlank() }
                )

                Log.d("QRScannerViewModel", "Sending link request: $linkRequest")

                // Make API call to link family
                val linkResult = repository.linkFamily(linkRequest)

                if (linkResult.success) {
                    _scanResult.value = linkResult
                    Log.d("QRScannerViewModel", "Family linked successfully")
                } else {
                    _errorMessage.value = linkResult.message ?: "Không thể kết nối"
                    hasProcessedQR = false
                }

            } catch (e: Exception) {
                Log.e("QRScannerViewModel", "Error processing QR code: ${e.message}", e)
                _errorMessage.value = "Lỗi xử lý: ${e.message}"
                hasProcessedQR = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
        hasProcessedQR = false
    }

    fun resetScanResult() {
        _scanResult.value = null
        hasProcessedQR = false
    }
}

