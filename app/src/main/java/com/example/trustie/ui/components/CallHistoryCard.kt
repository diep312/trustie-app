package com.example.trustie.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trustie.data.model.datamodel.CallHistoryItem

@Composable
fun CallHistoryCard(
    callItem: CallHistoryItem,
    onCallClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine status and colors based on call type
    val (statusText, statusColor) = when {
        callItem.isSuspicious -> "Lừa đảo" to Color(0xFFD32F2F) // Red for scam
        callItem.contactName == "Người lạ" -> "Nghi ngờ" to Color(0xFF8D6E63) // Brown for suspicious
        else -> "An toàn" to Color(0xFF4CAF50) // Green for safe
    }
    
    val displayName = when {
        callItem.isSuspicious -> "Người lạ"
        callItem.contactName == "Người lạ" -> "Người lạ"
        else -> callItem.contactName
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDBD5E8)), // Light purple background
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top row: Name and Phone Number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = displayName,
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = callItem.phoneNumber,
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Middle row: Time and Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = callItem.time,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = callItem.country,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom row: Call Type and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "📞 Cuộc gọi đến",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                
                // Status button
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = statusColor,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = statusText,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}