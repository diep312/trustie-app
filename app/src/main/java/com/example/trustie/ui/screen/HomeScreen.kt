package com.example.trustie.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trustie.ui.components.FeatureCard
import com.example.trustie.ui.model.FeatureItem
import com.example.trustie.ui.theme.TrustieTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onFeatureClick: (FeatureItem) -> Unit = {}
) {
    val features = listOf(
        FeatureItem(
            title = "Lịch sử cuộc gọi",
            icon = Icons.Default.Phone,
            backgroundColor = Color(0xFF2196F3),
            iconColor = Color.White,
            textColor = Color.White
        ),
        FeatureItem(
            title = "Cảnh báo SĐT",
            icon = Icons.Default.Security,
            backgroundColor = Color(0xFFF44336),
            iconColor = Color.White,
            textColor = Color.White
        ),
        FeatureItem(
            title = "Kiểm tra SĐT",
            icon = Icons.Default.PhoneAndroid,
            backgroundColor = Color(0xFF63B404),
            iconColor = Color.White,
            textColor = Color.White
        ),
        FeatureItem(
            title = "Kiểm tra web",
            icon = Icons.Default.TrendingUp,
            backgroundColor = Color(0xFFFF9800),
            iconColor = Color.White,
            textColor = Color.White
        ),
        FeatureItem(
            title = "Kết nối với người thân",
            icon = Icons.Default.People,
            backgroundColor = Color(0xFF5524A2),
            iconColor = Color.White,
            textColor = Color.White
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFDF2E9))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trang chủ",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            IconButton(
                onClick = { /* Handle notification click */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.Black,
                    modifier = Modifier.size(35.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Feature Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(features.take(4)) { feature ->
                FeatureCard(
                    feature = feature,
                    onClick = {
                        Log.d("NavigationDebug", "Clicked on feature: ${feature.title}")
                        onFeatureClick(feature)
                              },
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom single card
        features.getOrNull(4)?.let { feature ->
            FeatureCard(
                feature = feature,
                onClick = { onFeatureClick(feature) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TrustieTheme {
        HomeScreen()
    }
}