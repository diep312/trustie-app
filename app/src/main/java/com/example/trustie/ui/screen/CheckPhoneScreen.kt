package com.example.trustie.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trustie.ui.components.PhoneCheckResultCard
import com.example.trustie.ui.viewmodel.CheckPhoneViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.trustie.ui.theme.TrustieTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckPhoneScreen(
    onBackClick: () -> Unit,
    viewModel: CheckPhoneViewModel = viewModel()
) {
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val checkResult by viewModel.checkResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Log.d("CheckPhoneDebug", "CheckPhoneScreen recomposed")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF2E9))
    ) {
        // Header with back button
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(40.dp))
                    Text(
                        text = "Kiểm tra số",
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Loa",
                        modifier = Modifier.size(35.dp),
                        tint = Color(0xFF1A237E)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(35.dp),
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFFDF2E9)
            )
        )
        Spacer(modifier = Modifier.height(32.dp))
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Phone number input field
            Text(
                text = "Số điện thoại",
                fontSize = 35.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { viewModel.updatePhoneNumber(it) },
                modifier = Modifier
                    .fillMaxWidth(),
//
                placeholder = {
                    Text(
                        text = "Nhập số điện thoại cần kiểm tra",
                        fontSize = 22.sp,
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2196F3),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                )
            )


//            Spacer(modifier = Modifier.height(120.dp))
            if (phoneNumber.isNotBlank() && checkResult == null && !isLoading) {
                Spacer(modifier = Modifier.height(64.dp))
                Button(
                    onClick = { viewModel.checkPhoneNumber() },
                    modifier = Modifier
                        .widthIn(min = 200.dp)
                        .height(80.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F3280) // Green color
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    Text(
                        text = "Gửi",
                        color = Color.White,
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }



            Spacer(modifier = Modifier.height(32.dp))
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Đang kiểm tra số điện thoại...",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Error message
            errorMessage?.let { error ->
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
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Check result
            checkResult?.let { result ->
                PhoneCheckResultCard(
                    result = result,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(100.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Manual input button
                Button(
                    onClick = { viewModel.checkPhoneNumber() },
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dialpad,
                            contentDescription = "Manual input",
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Nhập số điện thoại",
                            fontSize = 25.sp,
                            lineHeight = 30.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Voice input button
                Button(
                    onClick = { viewModel.startVoiceInput() },
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice input",
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Nói số điện thoại",
                            fontSize = 25.sp,
                            lineHeight = 30.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
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



