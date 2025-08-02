
package com.example.trustie.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource // Import for painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel // Import hiltViewModel
import com.example.trustie.R
import com.example.trustie.ui.viewmodel.AuthViewModel
import com.example.trustie.ui.components.OTPDigitField
import com.example.trustie.ui.theme.TrustieTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPInputScreen(
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val otpCode by viewModel.otpCode.collectAsState()
    val authState by viewModel.authState.collectAsState()


    val isAuthenticated = authState.isAuthenticated


    val otpDigitCount = 4
    val focusRequesters = remember { List(otpDigitCount) { FocusRequester() } }

    Log.d("OTPInputScreen", "OTPInputScreen recomposed")

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            onNavigateToHome()
        }
    }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NHẬP OTP",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF208EE1),
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
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
        },
        containerColor = Color(0xFFFDF2E9)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .background(Color(0xFFFDF2E9)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Mã OTP đã được gửi\ntới quý khách",
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF208EE1),
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Mã OTP",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                repeat(otpDigitCount) { index ->
                    val currentDigit = otpCode.getOrNull(index)?.toString() ?: ""
                    OTPDigitField(
                        digit = currentDigit,
                        onValueChange = { newDigit ->
                            val currentOtpChars = otpCode.toMutableList()
                            while (currentOtpChars.size <= index) {
                                currentOtpChars.add(' ')
                            }

                            if (newDigit.isNotEmpty()) {
                                currentOtpChars[index] = newDigit.first()
                                viewModel.setOtpCode(currentOtpChars.joinToString("").replace(" ", "").trim()) // Gọi setOtpCode
                                if (index < otpDigitCount - 1) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            } else {
                                // Xử lý khi xóa ký tự
                                if (currentOtpChars[index] != ' ') {
                                    currentOtpChars[index] = ' '
                                    viewModel.setOtpCode(currentOtpChars.joinToString("").replace(" ", "").trim()) // Gọi setOtpCode
                                }
                                if (index > 0) {
                                    focusRequesters[index - 1].requestFocus()
                                }
                            }
                        },
                        focusRequester = focusRequesters[index]
                    )
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = {
                    if (otpCode.length == otpDigitCount) {
                        viewModel.verifyOtp()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !authState.isLoading && otpCode.length == otpDigitCount
            ) {
                if (authState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Gửi mã OTP",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { viewModel.sendOtp() },
                    modifier = Modifier.size(140.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !authState.isLoading,
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Gửi lại mã OTP",
                        tint = Color.White,
                        modifier = Modifier
                            .size(100.dp)
                            .offset(x = 10.dp, y = (-10).dp)
                            .graphicsLayer(rotationZ = -45f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Gửi lại mã OTP",
                    fontSize = 24.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            authState.errorMessage?.let { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = message,
                        fontSize = 14.sp,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            authState.successMessage?.let { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                ) {
                    Text(
                        text = message,
                        fontSize = 14.sp,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OTPInputScreenPreview() {
    TrustieTheme {
        OTPInputScreen(
            onNavigateToHome = {},
            onNavigateBack = {},
        )
    }
}
