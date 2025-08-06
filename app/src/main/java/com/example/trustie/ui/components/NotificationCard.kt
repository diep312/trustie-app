package com.example.trustie.ui.components

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
import com.example.trustie.data.model.NotificationItem

@Composable
fun NotificationCard(
    notification: NotificationItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFDBD5E8)        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = notification.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = notification.phoneNumber,
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = notification.time,
                    fontSize = 16.sp,
                    color = Color.Gray.copy(alpha = 0.8f)
                )

                Text(
                    text = notification.location,
                    fontSize = 14.sp,
                    color = Color.Gray.copy(alpha = 0.8f)
                )
            }
        }
    }
}
