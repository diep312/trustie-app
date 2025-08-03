package com.example.trustie.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QRCodeDisplay(
    qrCode: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(2.dp, Color(0xFF2196F3), RoundedCornerShape(16.dp)), // Viền xanh
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // Nền trắng
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // QR Code placeholder (in a real app, you'd use a QR code library)
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.White, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.size(180.dp)
                ) {
                    drawQRCodePattern(this)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hãy lấy máy người thân\nquét mã QR này để kết nối",
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

// Simple QR code pattern simulation (for demo purposes)
private fun drawQRCodePattern(drawScope: DrawScope) {
    val size = drawScope.size
    val cellSize = size.width / 25f

    // Draw a simple pattern that looks like a QR code
    for (i in 0 until 25) {
        for (j in 0 until 25) {
            // Create a pseudo-random pattern based on position
            val shouldFill = (i + j * 3 + i * j) % 3 == 0 ||
                    (i < 7 && j < 7) ||
                    (i > 17 && j < 7) ||
                    (i < 7 && j > 17)

            if (shouldFill) {
                drawScope.drawRect(
                    color = Color.Black,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        i * cellSize,
                        j * cellSize
                    ),
                    size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                )
            }
        }
    }
}