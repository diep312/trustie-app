package com.example.trustie.ui.screen.home


import android.util.Log
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trustie.R
import com.example.trustie.ui.components.FeatureCard
import com.example.trustie.ui.components.NotificationButton
import com.example.trustie.ui.components.ShieldIcon
import com.example.trustie.data.model.FeatureItem
import com.example.trustie.ui.components.ScamShieldBanner
import com.example.trustie.ui.theme.TrustieTheme
import com.example.trustie.ui.screen.home.HomeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onFeatureClick: (FeatureItem) -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val isAlertEnabled by viewModel.isAlertEnabled.collectAsState()
    val showAlertDialog by viewModel.showAlertDialog.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val features = listOf(
        FeatureItem(
            title = "Lịch sử gọi",
            iconResId = R.drawable.ic_phone,
            backgroundColor = Color(0xFF2196F3),
            iconColor = Color.White,
            textColor = Color.White
        ),
        FeatureItem(
            title = "Báo cáo số",
            iconResId = R.drawable.ic_shield_info,
            backgroundColor = Color(0xFFF44336),
            iconColor = Color.White,
            textColor = Color.White
        ),
        FeatureItem(
            title = "Kết nối người thân",
            iconResId = R.drawable.ic_people,
            backgroundColor = Color(0xFF1565C0),
            iconColor = Color.White,
            textColor = Color.White
        ),
        FeatureItem(
            title = "Kiểm tra ảnh",
            iconResId = R.drawable.ic_check_photo,
            backgroundColor = Color(0xFFFF9800),
            iconColor = Color.White,
            textColor = Color.White
        ),
        FeatureItem(
            title = "Kiểm tra số",
            iconResId = R.drawable.ic_phone_square,
            backgroundColor = Color(0xFF63B404),
            iconColor = Color.White,
            textColor = Color.White
        ),
        FeatureItem(
            title="Nhận diện cuộc gọi lừa đảo",
            iconResId = R.drawable.ic_phone_deny,
            backgroundColor = Color(0xFF21B475),
            iconColor = Color.White,
            textColor = Color.White
        )
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

    Scaffold(modifier =
        modifier.background(Color(0xFFFDF2E9))
            .padding(16.dp, 0.dp))
    { padding ->
        Column(
            modifier = modifier
                .fillMaxSize().background(Color(0xFFFDF2E9))
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Chúc quý vị ngày an lành",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    lineHeight = 43.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                NotificationButton(
                    onClick = onNotificationClick,
                    hasUnreadNotifications = false,
                    modifier = Modifier, // no weight
                    width = 96.dp,
                    height = 84.dp
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            ScamShieldBanner(
                isEnabled = isAlertEnabled,
                onToggle = { viewModel.toggleAlert() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(features.take(6)) { feature ->
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


//            Spacer(modifier = Modifier.height(8.dp))
//
//
//            features.getOrNull(4)?.let { feature ->
//                FeatureCard(
//                    feature = feature,
//                    onClick = { onFeatureClick(feature) },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(160.dp)
//                )
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Button(
//                onClick = onLogoutClick,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFFD32F2F)
//                ),
//                shape = MaterialTheme.shapes.medium
//            ) {
//                Text(
//                    text = "Đăng xuất",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.White
//                )
//            }
        }
    }
}


@Preview(showBackground = false)
@Composable
fun HomeScreenPreview() {
    TrustieTheme {
        HomeScreen(onLogoutClick = {})
    }
}

