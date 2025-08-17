package com.example.trustie.ui.screen.qrscreen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QrScannerViewModel @Inject constructor() : ViewModel() {

    var isLoading: Boolean = false
        private set

    // Later you can attach an ImageAnalyzer here and update loading
    // when you actually detect a QR code
    fun onQrCodeDetected(result: String) {
    }
}