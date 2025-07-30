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

//    Card(
//        modifier = modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        colors = CardDefaults.cardColors(containerColor = backgroundColor)
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Icon(
//                    imageVector = icon,
//                    contentDescription = null,
//                    tint = textColor,
//                    modifier = Modifier.size(24.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    text = result.phoneNumber,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Black
//                )
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Text(
//                text = when (result.riskLevel) {
//                    "HIGH" -> "⚠️ Số nguy hiểm"
//                    "MEDIUM" -> "⚠️ Cần cẩn thận"
//                    else -> "✅ Số an toàn"
//                },
//                fontSize = 16.sp,
//                fontWeight = FontWeight.SemiBold,
//                color = textColor
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            if (result.reportCount > 0) {
//                Text(
//                    text = "Số lượng báo cáo: ${result.reportCount}",
//                    fontSize = 14.sp,
//                    color = Color.Black.copy(alpha = 0.7f)
//                )
//            }
//
//            result.lastReported?.let {
//                Text(
//                    text = "Báo cáo gần nhất: $it",
//                    fontSize = 14.sp,
//                    color = Color.Black.copy(alpha = 0.7f)
//                )
//            }
//
//            result.description?.let {
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    text = it,
//                    fontSize = 14.sp,
//                    color = Color.Black.copy(alpha = 0.8f)
//                )
//            }
//        }
//    }
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
