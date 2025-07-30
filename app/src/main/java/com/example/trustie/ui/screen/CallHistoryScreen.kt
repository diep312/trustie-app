

package com.example.trustie.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.trustie.ui.components.CallHistoryCard
import com.example.trustie.ui.theme.TrustieTheme
import com.example.trustie.ui.viewmodel.CallHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallHistoryScreen(
    onBackClick: () -> Unit,
    viewModel: CallHistoryViewModel = viewModel()
) {
    val callHistory by viewModel.callHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("CallHistoryDebug", "CallHistoryScreen LaunchedEffect triggered")
        viewModel.loadCallHistory()
    }


    DisposableEffect(callHistory, isLoading, errorMessage) {
        Log.d("CallHistoryDebug", "CallHistoryScreen recomposed. isLoading: $isLoading, errorMessage: $errorMessage, callHistory size: ${callHistory.size}")
        onDispose { /* cleanup if needed */ }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF2E9))
    ) {

        TopAppBar(
            title = {
                Text(
                    text = "  Lịch sử cuộc gọi",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(40.dp),
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFFDF2E9)
            )
        )

        when {
            isLoading -> {
                Log.d("CallHistoryDebug", "Displaying loading indicator.")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF2196F3)
                    )
                }
            }

            errorMessage != null -> {
                Log.d("CallHistoryDebug", "Displaying error message: $errorMessage")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Có lỗi xảy ra!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F), // Màu đỏ đậm
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = errorMessage ?: "Không thể tải dữ liệu. Vui lòng thử lại.",
                            fontSize = 16.sp,
                            color = Color.Black.copy(alpha = 0.7f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadCallHistory() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            )
                        ) {
                            Text("Thử lại")
                        }
                    }
                }
            }

            callHistory.isEmpty() -> {
                Log.d("CallHistoryDebug", "Call history is empty. Displaying empty message.")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không có dữ liệu lịch sử cuộc gọi.",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }

            else -> {
                Log.d("CallHistoryDebug", "Displaying call history list. Size: ${callHistory.size}")
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(callHistory) { call ->
                        CallHistoryCard(
                            callItem = call,
                            onCallClick = { phoneNumber ->
                                viewModel.makeCall(phoneNumber)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CallHistoryPreview() {
    TrustieTheme {
        CallHistoryScreen(onBackClick = {})
    }
}


