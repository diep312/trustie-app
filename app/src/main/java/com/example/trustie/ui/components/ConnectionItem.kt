//package com.example.trustie.ui.components
//
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.drawscope.DrawScope
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.trustie.ui.model.RelativeConnection
//
//@Composable
//fun ConnectionItem(
//    connection: RelativeConnection,
//    onRemove: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        modifier = modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = if (connection.isConnected) Color(0xFFE8F5E8) else Color(0xFFFFF3E0)
//        )
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                imageVector = Icons.Default.Person,
//                contentDescription = "Person",
//                tint = if (connection.isConnected) Color(0xFF4CAF50) else Color(0xFFFF9800),
//                modifier = Modifier.size(32.dp)
//            )
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = connection.name,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Black
//                )
//                Text(
//                    text = connection.relationship,
//                    fontSize = 14.sp,
//                    color = Color.Gray
//                )
//                Text(
//                    text = connection.phoneNumber,
//                    fontSize = 14.sp,
//                    color = Color.Gray
//                )
//                if (connection.isConnected && connection.connectedAt != null) {
//                    Text(
//                        text = "Kết nối lúc: ${connection.connectedAt}",
//                        fontSize = 12.sp,
//                        color = Color(0xFF4CAF50)
//                    )
//                } else {
//                    Text(
//                        text = "Chưa kết nối",
//                        fontSize = 12.sp,
//                        color = Color(0xFFFF9800)
//                    )
//                }
//            }
//
//            IconButton(
//                onClick = { onRemove(connection.id) }
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Delete,
//                    contentDescription = "Remove connection",
//                    tint = Color(0xFFD32F2F)
//                )
//            }
//        }
//    }
//}
//@Composable
//fun QRCodeDisplay(
//    qrCode: String,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        modifier = modifier,
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8FF)),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Column(
//            modifier = Modifier.padding(24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // QR Code placeholder (in a real app, you'd use a QR code library)
//            Box(
//                modifier = Modifier
//                    .size(200.dp)
//                    .background(Color.White, RoundedCornerShape(8.dp)),
//                contentAlignment = Alignment.Center
//            ) {
//                Canvas(
//                    modifier = Modifier.size(180.dp)
//                ) {
//                    drawQRCodePattern(this)
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = "Hãy lấy máy người thân\nquét mã QR này để kết nối",
//                fontSize = 16.sp,
//                color = Color.Black.copy(alpha = 0.7f),
//                textAlign = TextAlign.Center,
//                lineHeight = 22.sp
//            )
//        }
//    }
//}
//
//
//private fun drawQRCodePattern(drawScope: DrawScope) {
//    val size = drawScope.size
//    val cellSize = size.width / 25f
//
//
//    for (i in 0 until 25) {
//        for (j in 0 until 25) {
//
//            val shouldFill = (i + j * 3 + i * j) % 3 == 0 ||
//                    (i < 7 && j < 7) ||
//                    (i > 17 && j < 7) ||
//                    (i < 7 && j > 17)
//
//            if (shouldFill) {
//                drawScope.drawRect(
//                    color = Color.Black,
//                    topLeft = androidx.compose.ui.geometry.Offset(
//                        i * cellSize,
//                        j * cellSize
//                    ),
//                    size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
//                )
//            }
//        }
//    }
//}



package com.example.trustie.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trustie.data.model.RelativeConnection

@Composable
fun RelativeConnectionItem(
    connection: RelativeConnection,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar tròn với chữ cái đầu
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(connection.avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = connection.initials,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = connection.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = connection.phoneNumber,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
        // Đường kẻ phân cách
        Divider(
            color = Color.Gray.copy(alpha = 0.3f),
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 0.dp)
        )
    }
}