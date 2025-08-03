package com.example.trustie.ui.screen.auth

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trustie.ui.theme.TrustieTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PhoneInputScreen(
    onNavigateToOTP: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val authState by viewModel.authState.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    Log.d("PhoneInputScreen", "PhoneInputScreen recomposed")

    LaunchedEffect(authState.isOtpSent) {
        if (authState.isOtpSent) {
            onNavigateToOTP()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF2E9))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Xin vui lòng nhập\nsố điện thoại",
            fontSize = 35.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF208EE1),
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )
        Spacer(modifier = Modifier.height(80.dp))
        Text(
            text = "Số điện thoại",
            fontSize = 25.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { viewModel.setPhoneNumber(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    Log.d("KeyboardDebug", "TextField focus state: ${focusState.isFocused}")
                },
            textStyle = TextStyle(
                fontSize = 32.sp,
                lineHeight = 36.sp,
                color = Color.Black
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE0E0E0),
                unfocusedContainerColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color(0xFF2196F3),
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { viewModel.sendOtp() },
            modifier = Modifier
                .widthIn(min = 200.dp)
                .height(80.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3)
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = !authState.isLoading && phoneNumber.isNotEmpty()
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Gửi",
                    color = Color.White,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        Log.d("KeyboardDebug", "Dialpad button clicked.")
                        scope.launch {
                            focusRequester.requestFocus()
                            delay(100)
                            keyboardController?.show()
                            Log.d("KeyboardDebug", "Requested focus and showed keyboard.")
                        }
                    },
                    modifier = Modifier.size(140.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = true
                ) {
                    Icon(
                        imageVector = Icons.Default.Dialpad,
                        contentDescription = "Dialpad",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Bàn phím",
                    fontSize = 24.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {  },
                    modifier = Modifier.size(140.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = true
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Microphone",
                            tint = Color.White,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Giọng nói",
                    fontSize = 24.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
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

@Preview(showBackground = true)
@Composable
fun PhoneInputScreenPreview() {
    TrustieTheme {
        PhoneInputScreen(
            onNavigateToOTP = {},
        )
    }
}

