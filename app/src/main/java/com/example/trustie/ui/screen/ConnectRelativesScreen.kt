package com.example.trustie.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trustie.navigation.Screen
import com.example.trustie.ui.components.ConnectionItem
import com.example.trustie.ui.components.QRCodeDisplay
import com.example.trustie.ui.theme.TrustieTheme
import com.example.trustie.ui.viewmodel.ConnectRelativesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectRelativesScreen(
    onBackClick: () -> Unit,
    viewModel: ConnectRelativesViewModel = viewModel()
) {
    val qrCode by viewModel.qrCode.collectAsState()
    val connections by viewModel.connections.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val showConnections by viewModel.showConnections.collectAsState()

    Log.d("ConnectionDebug", "ConnectRelativesScreen recomposed")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF2E9))
    ) {
        // Header with back button
        TopAppBar(
            title = {
                Text(
                    text = "Kết nối người thân",
                    fontSize = 33.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier.size(35.dp)
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { viewModel.toggleConnectionsView() }
                ) {
                    Icon(
                        imageVector = if (showConnections) Icons.Default.QrCode else Icons.Default.List,
                        contentDescription = if (showConnections) "Show QR Code" else "Show Connections",
                        tint = Color.Black,
                        modifier = Modifier.size(33.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFFDF2E9)
            )
        )

        // Main content
        if (showConnections) {
            // Show connections list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Text(
                        text = "Danh sách kết nối",
                        fontSize = 22.sp, // Tăng kích thước chữ
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                items(connections) { connection ->
                    ConnectionItem(
                        connection = connection,
                        onRemove = { viewModel.removeConnection(it) }
                    )
                }

                if (connections.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Chưa có kết nối nào",
                                fontSize = 20.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        } else {
            // Show QR code
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(32.dp))

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
                                Button(
                                    onClick = {
                                        viewModel.clearError()
                                        viewModel.generateQRCode()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2196F3)
                                    )
                                ) {
                                    Text(
                                        text = "Thử lại",
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }

                    qrCode != null -> {
                        QRCodeDisplay(
                            qrCode = qrCode!!,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.generateQRCode() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text(
                                text = "Tạo mã QR mới",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
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
