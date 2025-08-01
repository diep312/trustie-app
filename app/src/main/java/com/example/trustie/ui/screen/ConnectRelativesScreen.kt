





package com.example.trustie.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trustie.R
import com.example.trustie.ui.components.QRCodeDisplay
import com.example.trustie.ui.components.RelativeConnectionItem
import com.example.trustie.ui.theme.TrustieTheme
import com.example.trustie.ui.viewmodel.ConnectRelativesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectRelativesScreen(
    onBackClick: () -> Unit,
    viewModel: ConnectRelativesViewModel = viewModel()
) {
    Log.d("ConnectRelativesScreen", "ConnectRelativesScreen is being composed!")

    val qrCode by viewModel.qrCode.collectAsState()
    val connections by viewModel.connections.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Log.d("ConnectionDebug", "ConnectRelativesScreen recomposed")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF2E9))
    ) {

        TopAppBar(
            title = {
                Text(
                    text = "KẾT NỐI",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF208EE1),
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(60.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.back_buttonn),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(120.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFFDF2E9)
            )
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
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
                        val borderColor = Color(0xFF208EE1)
                        Column(
                            modifier = Modifier
                                .padding(16.dp),

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
                    Box(
                        modifier = Modifier
                            .border(
                                width = 8.dp,
                                color = Color(0xFF208EE1),
                                shape = RoundedCornerShape(30.dp)
                            )
                            .padding(8.dp)
                    ) {
                        QRCodeDisplay(
                            qrCode = qrCode!!,
                            modifier = Modifier.size(250.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.generateQRCode() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .widthIn(min = 300.dp)
                            .height(60.dp)
                    ) {
                        Text(
                            text = "Nhận QR kết nối",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))


            Text(
                text = "Danh sách kết nối",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5495C4),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Danh sách kết nối
            if (connections.isEmpty()) {
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
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    userScrollEnabled = false
                ) {
                    items(connections) { connection ->
                        RelativeConnectionItem(
                            connection = connection
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
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