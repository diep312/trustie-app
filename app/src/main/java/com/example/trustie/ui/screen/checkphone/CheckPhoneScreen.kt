package com.example.trustie.ui.screen.checkphone

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trustie.R
import com.example.trustie.ui.components.PhoneCheckResultCard
import com.example.trustie.ui.components.ScreenHeader
import com.example.trustie.ui.components.PhoneNumberInputField
import com.example.trustie.ui.components.ActionButton
import com.example.trustie.ui.theme.TrustieTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckPhoneScreen(
    onBackClick: () -> Unit,
    viewModel: CheckPhoneViewModel = hiltViewModel()
) {
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val checkResult by viewModel.checkResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Log.d("CheckPhoneDebug", "CheckPhoneScreen recomposed")

    Scaffold{ padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDF2E9))
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            ScreenHeader(
                title = "Kiểm tra",
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Phone number label
                Text(
                    text = "Số điện thoại",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Phone number input field
                PhoneNumberInputField(
                    value = phoneNumber,
                    onValueChange = { viewModel.updatePhoneNumber(it) },
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    FloatingActionButton(
                        onClick = { viewModel.checkPhoneNumber() },
                        containerColor = if (phoneNumber.isNotBlank()) Color(0xFF2196F3) else Color.Gray,
                        contentColor = Color.White,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_send),
                            contentDescription = "Send report",
                            modifier = Modifier.size(32.dp)
                        )

                    }
                }

                // Loading indicator
                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        color = Color(0xFF2196F3)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Đang kiểm tra số điện thoại...",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // Error message
                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Text(
                            text = error,
                            color = Color(0xFFD32F2F),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Check result
                checkResult?.let { result ->
                    Spacer(modifier = Modifier.height(16.dp))
                    PhoneCheckResultCard(
                        result = result,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckPhonePreview() {
    TrustieTheme {
        CheckPhoneScreen(onBackClick = {})
    }
}
