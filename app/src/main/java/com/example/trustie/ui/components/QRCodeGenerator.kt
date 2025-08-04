package com.example.trustie.ui.components

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun QRCodeGenerator(
    data: String,
    modifier: Modifier = Modifier,
    size: Int = 250
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(data) {
        scope.launch {
            isLoading = true
            try {
                qrBitmap = withContext(Dispatchers.IO) {
                    generateQRCode(data, size)
                }
            } catch (e: Exception) {
                // Handle QR generation error
                Log.e("QRCodeGenerator", "Error generating QR code", e)
            } finally {
                isLoading = false
            }
        }
    }

    Card(
        modifier = modifier
            .border(
                width = 4.dp,
                color = androidx.compose.ui.graphics.Color(0xFF2196F3),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = androidx.compose.ui.graphics.Color(0xFF2196F3),
                    modifier = Modifier.size(48.dp)
                )
            } else {
                qrBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: run {
                    // Fallback when QR generation fails
                    Text(
                        text = "QR Code\nGeneration\nFailed",
                        color =  androidx.compose.ui.graphics.Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

private fun generateQRCode(data: String, size: Int): Bitmap {
    val hints = hashMapOf<EncodeHintType, Any>().apply {
        put(EncodeHintType.MARGIN, 1)
    }
    
    val bits = QRCodeWriter().encode(
        data,
        BarcodeFormat.QR_CODE,
        size,
        size,
        hints
    )
    
    val width = bits.width
    val height = bits.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
        }
    }
    
    return bitmap
} 