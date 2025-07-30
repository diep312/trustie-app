package com.example.trustie.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.repository.ConnectionRepository
import com.example.trustie.ui.model.RelativeConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConnectRelativesViewModel(
    private val repository: ConnectionRepository = ConnectionRepository()
) : ViewModel() {

    private val _qrCode = MutableStateFlow<String?>(null)
    val qrCode: StateFlow<String?> = _qrCode.asStateFlow()

    private val _connections = MutableStateFlow<List<RelativeConnection>>(emptyList())
    val connections: StateFlow<List<RelativeConnection>> = _connections.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showConnections = MutableStateFlow(false)
    val showConnections: StateFlow<Boolean> = _showConnections.asStateFlow()

    init {
        Log.d("ConnectionDebug", "ConnectRelativesViewModel initialized")
        generateQRCode()
        loadConnections()
    }

    fun generateQRCode() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("ConnectionDebug", "Generating QR code...")
                val response = repository.generateQRCode()

                if (response.success && response.qrCode != null) {
                    _qrCode.value = response.qrCode
                    Log.d("ConnectionDebug", "QR code generated successfully")
                } else {
                    _errorMessage.value = response.message ?: "Không thể tạo mã QR"
                    Log.e("ConnectionDebug", "Failed to generate QR code: ${response.message}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
                Log.e("ConnectionDebug", "Exception generating QR code: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadConnections() {
        viewModelScope.launch {
            try {
                Log.d("ConnectionDebug", "Loading connections...")
                val response = repository.getConnections()

                if (response.success) {
                    _connections.value = response.connections
                    Log.d("ConnectionDebug", "Loaded ${response.connections.size} connections")
                } else {
                    _errorMessage.value = response.message ?: "Không thể tải danh sách kết nối"
                    Log.e("ConnectionDebug", "Failed to load connections: ${response.message}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
                Log.e("ConnectionDebug", "Exception loading connections: ${e.message}", e)
            }
        }
    }

    fun toggleConnectionsView() {
        _showConnections.value = !_showConnections.value
        Log.d("ConnectionDebug", "Toggled connections view: ${_showConnections.value}")
    }

    fun removeConnection(connectionId: String) {
        viewModelScope.launch {
            try {
                Log.d("ConnectionDebug", "Removing connection: $connectionId")
                val success = repository.removeConnection(connectionId)

                if (success) {
                    // Remove from local list
                    _connections.value = _connections.value.filter { it.id != connectionId }
                    Log.d("ConnectionDebug", "Connection removed successfully")
                } else {
                    _errorMessage.value = "Không thể xóa kết nối"
                    Log.e("ConnectionDebug", "Failed to remove connection")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
                Log.e("ConnectionDebug", "Exception removing connection: ${e.message}", e)
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
