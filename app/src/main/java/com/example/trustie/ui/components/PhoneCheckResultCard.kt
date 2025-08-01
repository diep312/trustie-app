package com.example.trustie.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trustie.ui.model.PhoneCheckItem

@Composable
fun PhoneCheckResultCard(
    result: PhoneCheckItem,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, icon) = when (result.riskLevel) {
        "HIGH" -> Triple(Color(0xFFFFEBEE), Color(0xFFD32F2F), Icons.Default.Warning)
        "MEDIUM" -> Triple(Color(0xFFFFF3E0), Color(0xFFFF9800), Icons.Default.Warning)
        else -> Triple(Color(0xFFE8F5E8), Color(0xFF4CAF50), Icons.Default.CheckCircle)
    }


    Text(
        text = when (result.riskLevel) {
            "HIGH" -> "Có dấu hiệu lừa đảo"
            "MEDIUM" -> "Cần cẩn thận"
            else -> "Số an toàn"
        },
        fontSize = 35.sp,
        fontWeight = FontWeight.SemiBold,
        color = textColor
    )
}