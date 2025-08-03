package com.example.trustie.ui.screen.home


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trustie.R
import com.example.trustie.ui.components.FeatureCard
import com.example.trustie.data.model.FeatureItem
import com.example.trustie.ui.theme.TrustieTheme
import com.example.trustie.ui.screen.home.HomeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onFeatureClick: (FeatureItem) -> Unit = {},
    onLogoutClick: () -> Unit,
    onNotificationClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val isAlertEnabled by viewModel.isAlertEnabled.collectAsState()
    val showAlertDialog by viewModel.showAlertDialog.collectAsState()
    val features = listOf(
        FeatureItem(
            title = "Lịch sử gọi",
            icon = Icons.Default.Phone,
            backgroundColor = Color(0xFF2196F3),
            iconColor = Color.White,
            textColor = Color.White
        ),
        FeatureItem(
            title = "Báo cáo số",
            icon = Icons.Default.Security,
            backgroundColor = Color(0xFFF44336),
            iconColor = Color.White,
            textColor = Color.White
        ),
        FeatureItem(
            title = "Kết nối",
            icon = Icons.Default.People,
            backgroundColor = Color(0xFF1565C0),
            iconColor = Color.White,
            textColor = Color.White
        ),
        FeatureItem(
            title = "Kiểm tra ảnh",
            icon = Icons.Default.ImageSearch,
            backgroundColor = Color(0xFFFF9800),
            iconColor = Color.White,
            textColor = Color.White
        ),
        FeatureItem(
            title = "Kiểm tra Số",
            icon = Icons.Default.PhoneAndroid,
            backgroundColor = Color(0xFF63B404),
            iconColor = Color.White,
            textColor = Color.White
        ),
    )

    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissAlertDialog() },
            title = {
                Text(
                    text = "Thông báo",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
            },
            text = {
                Text(
                    text = if (isAlertEnabled)
                        "Đã bật chế độ cảnh báo an toàn!"
                    else
                        "Đã tắt chế độ cảnh báo an toàn!",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.dismissAlertDialog() }
                ) {
                    Text(
                        text = "OK",
                        color = Color(0xFF2196F3),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(12.dp)
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFDF2E9))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_page1),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(220.dp)
                    .heightIn(max = 80.dp)
                    .padding(8.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Thông báo",
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(48.dp)
                    )
                }
                Text(
                    text = "Thông báo",
                    fontSize = 14.sp,
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF005893),
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
//
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF005893),
                                Color(0xFF1075AB),
                                Color(0xFF0E85CE),
                                Color(0xFF36AAF6),
                                Color(0xFF68B9F5),
                                Color(0xFF95CFFF)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Text(
                        text = "  BẬT BẢO VỆ AN TOÀN",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {  viewModel.toggleAlert() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.width(140.dp)
                        ) {
                            Text(
                                text = if (isAlertEnabled) "Đã bật" else "Bật ngay",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Image(
                            painter = painterResource(id = R.drawable.ex_image1),
                            contentDescription = "Hình minh họa",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(260.dp)
                        )
                    }
                }
            }
        }


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


        Spacer(modifier = Modifier.height(8.dp))


        features.getOrNull(4)?.let { feature ->
            FeatureCard(
                feature = feature,
                onClick = { onFeatureClick(feature) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD32F2F)
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Đăng xuất",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TrustieTheme {
        HomeScreen(onLogoutClick = {})
    }
}

