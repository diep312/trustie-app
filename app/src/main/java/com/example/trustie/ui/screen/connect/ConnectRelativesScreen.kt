//package com.example.trustie.ui.screen.connect
//
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.example.trustie.ui.components.ConnectionCard
//import com.example.trustie.ui.components.CustomButton
//import com.example.trustie.ui.components.QRCodeGenerator
//import com.example.trustie.ui.components.ScreenHeader
//import com.example.trustie.ui.theme.TrustieTheme
//import com.example.trustie.ui.screen.connect.ConnectRelativesViewModel
//import com.example.trustie.utils.UserUtils
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ConnectRelativesScreen(
//    onBackClick: () -> Unit,
//    viewModel: ConnectRelativesViewModel = hiltViewModel()
//) {
//    Log.d("ConnectRelativesScreen", "ConnectRelativesScreen is being composed!")
//
//    val connections by viewModel.connections.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val errorMessage by viewModel.errorMessage.collectAsState()
//
//    // Get current user ID for QR code generation
//    val userId = UserUtils.getCurrentUserId()
//
//    Log.d("ConnectionDebug", "ConnectRelativesScreen recomposed")
//
//    Scaffold(
//        modifier = Modifier.fillMaxSize(),
//        containerColor = Color(0xFFFDF2E9)
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .background(Color(0xFFFDF2E9))
//        ) {
//            // Custom Header
//            ScreenHeader(
//                title = "Kết nối",
//                onBackClick = onBackClick,
//                backgroundColor = Color(0xFFFDF2E9),
//                titleColor = Color.Black,
//                returnText = "Quay về"
//            )
//
//            Spacer(modifier = Modifier.height(40.dp))
//
//            // Main Content
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(horizontal = 24.dp)
//                    .verticalScroll(rememberScrollState()),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                // QR Code Section
//                when {
//                    isLoading -> {
//                        CircularProgressIndicator(
//                            color = Color(0xFF2196F3),
//                            modifier = Modifier.size(48.dp)
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Text(
//                            text = "Đang tạo mã QR...",
//                            fontSize = 18.sp,
//                            color = Color.Gray
//                        )
//                    }
//
//                    errorMessage != null -> {
//                        Card(
//                            modifier = Modifier.fillMaxWidth(),
//                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
//                        ) {
//                            Column(
//                                modifier = Modifier.padding(16.dp),
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Text(
//                                    text = "Có lỗi xảy ra!",
//                                    fontSize = 20.sp,
//                                    fontWeight = FontWeight.Bold,
//                                    color = Color(0xFFD32F2F)
//                                )
//                                Spacer(modifier = Modifier.height(8.dp))
//                                Text(
//                                    text = errorMessage!!,
//                                    fontSize = 16.sp,
//                                    color = Color(0xFFD32F2F)
//                                )
//                                Spacer(modifier = Modifier.height(16.dp))
//                                CustomButton(
//                                    text = "Thử lại",
//                                    onClick = {
//                                        viewModel.clearError()
//                                        viewModel.generateQRCode()
//                                    },
//                                    modifier = Modifier
//                                        .width(200.dp)
//                                        .height(48.dp)
//                                )
//                            }
//                        }
//                    }
//
//                    else -> {
//                        // QR Code Display
//                        QRCodeGenerator(
//                            data = "trustie\\:connect=family_user_id=$userId",
//                            modifier = Modifier.size(250.dp)
//                        )
//
//                        Spacer(modifier = Modifier.height(24.dp))
//
//                        // Generate QR Button
//                        CustomButton(
//                            text = "Nhận QR kết nối",
//                            onClick = { viewModel.generateQRCode() },
//                            modifier = Modifier
//                                .width(300.dp)
//                                .height(56.dp)
//                        )
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(32.dp))
//
//                // Separator
//                Divider(
//                    color = Color.Gray.copy(alpha = 0.3f),
//                    thickness = 1.dp,
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                // Connection List Section
//                Text(
//                    text = "Danh sách kết nối",
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Black,
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Connection List
//                if (connections.isEmpty()) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(32.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "Chưa có kết nối nào",
//                            fontSize = 18.sp,
//                            color = Color.Gray
//                        )
//                    }
//                } else {
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .heightIn(max = 400.dp),
//                        verticalArrangement = Arrangement.spacedBy(12.dp),
//                        userScrollEnabled = false
//                    ) {
//                        items(connections) { connection ->
//                            ConnectionCard(
//                                name = connection.name,
//                                phoneNumber = connection.phoneNumber,
//                                initials = connection.initials,
//                                avatarColor = connection.avatarColor
//                            )
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(32.dp))
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun ConnectRelativesPreview() {
//    TrustieTheme {
//        ConnectRelativesScreen(onBackClick = {})
//    }
//}

package com.example.trustie.ui.screen.connect

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trustie.ui.components.ConnectionCard
import com.example.trustie.ui.components.CustomButton
import com.example.trustie.ui.components.QRCodeGenerator
import com.example.trustie.ui.components.ScreenHeader
import com.example.trustie.ui.theme.TrustieTheme
import com.example.trustie.ui.screen.connect.ConnectRelativesViewModel
import com.example.trustie.utils.UserUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectRelativesScreen(
    onBackClick: () -> Unit,
    onNavigateToQRScanner: () -> Unit = {},
    viewModel: ConnectRelativesViewModel = hiltViewModel()
) {
    Log.d("ConnectRelativesScreen", "ConnectRelativesScreen is being composed!")
    val connections by viewModel.connections.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Get current user ID for QR code generation
    val userId = UserUtils.getCurrentUserId()
    Log.d("ConnectionDebug", "ConnectRelativesScreen recomposed")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFFDF2E9)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFDF2E9))
        ) {
            // Custom Header
            ScreenHeader(
                title = "Kết nối",
                onBackClick = onBackClick,
                backgroundColor = Color(0xFFFDF2E9),
                titleColor = Color.Black,
                returnText = "Quay về"
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // QR Code Section
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            color = Color(0xFF2196F3),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Đang tạo mã QR...",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }
                    errorMessage != null -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Có lỗi xảy ra!",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD32F2F)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = errorMessage!!,
                                    fontSize = 16.sp,
                                    color = Color(0xFFD32F2F)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                CustomButton(
                                    text = "Thử lại",
                                    onClick = {
                                        viewModel.clearError()
                                        viewModel.generateQRCode()
                                    },
                                    modifier = Modifier
                                        .width(200.dp)
                                        .height(48.dp)
                                )
                            }
                        }
                    }
                    else -> {
                        // QR Code Display
                        QRCodeGenerator(
                            data = "trustie\\:connect=family_user_id=$userId",
                            modifier = Modifier.size(250.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Generate QR Button
                        CustomButton(
                            text = "Nhận QR kết nối",
                            onClick = { viewModel.generateQRCode() },
                            modifier = Modifier
                                .width(300.dp)
                                .height(56.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // QR Scanner Button for family members
                        CustomButton(
                            text = "Quét mã QR người thân",
                            onClick = onNavigateToQRScanner,
                            backgroundColor = Color(0xFF4CAF50),
                            modifier = Modifier
                                .width(300.dp)
                                .height(56.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Separator
                Divider(
                    color = Color.Gray.copy(alpha = 0.3f),
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Connection List Section
                Text(
                    text = "Danh sách kết nối",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Connection List
                if (connections.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Chưa có kết nối nào",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        userScrollEnabled = false
                    ) {
                        items(connections) { connection ->
                            ConnectionCard(
                                name = connection.name,
                                phoneNumber = connection.phoneNumber,
                                initials = connection.initials,
                                avatarColor = connection.avatarColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConnectRelativesPreview() {
    TrustieTheme {
        ConnectRelativesScreen(onBackClick = {})
    }
}
