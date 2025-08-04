package com.example.trustie.ui.screen.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trustie.ui.theme.TrustieTheme

@Composable
fun IncomingCallAlertScreen(
    phoneNumber: String,
    isSuspicious: Boolean,
    onAcceptCall: () -> Unit,
    onDeclineCall: () -> Unit
) {
    val backgroundColor = if (isSuspicious) {
        Brush.verticalGradient(
            colors = listOf(Color(0xFFE53935), Color(0xFFB71C1C))
        )
    } else {

        Brush.verticalGradient(
            colors = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = "Cuộc gọi đến",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = phoneNumber,
                fontSize = 32.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Suspicious warning
            if (isSuspicious) {
                Text(
                    text = "NGHI NGỜ LỪA ĐẢO",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 48.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decline Button
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = onDeclineCall,
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)) // Đỏ
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Từ chối",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Từ chối",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Accept Button
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = onAcceptCall,
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Xanh lá
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Chấp nhận",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Chấp nhận",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IncomingCallAlertScreenPreview() {
    TrustieTheme {
        IncomingCallAlertScreen(
            phoneNumber = "+84 123 456 789",
            isSuspicious = true,
            onAcceptCall = {},
            onDeclineCall = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IncomingCallAlertScreenNormalCallPreview() {
    TrustieTheme {
        IncomingCallAlertScreen(
            phoneNumber = "0987654321",
            isSuspicious = false,
            onAcceptCall = {},
            onDeclineCall = {}
        )
    }
}
