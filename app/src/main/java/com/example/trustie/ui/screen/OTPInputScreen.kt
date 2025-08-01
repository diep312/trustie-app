

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trustie.R
import com.example.trustie.ui.viewmodel.AuthViewModel
import com.example.trustie.ui.components.OTPDigitField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPInputScreen(
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel
) {
    val otpCode by viewModel.otpCode.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState() // Giữ lại phoneNumber nếu cần, dù không hiển thị
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    val focusRequesters = remember { List(4) { FocusRequester() } }

    Log.d("OTPInputScreen", "OTPInputScreen recomposed")

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
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
                        onClick = {},
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
                repeat(4) { index ->
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
                                viewModel.updateOTPCode(currentOtpChars.joinToString("").replace(" ", "").trim())

                                if (index < 3) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            } else {
                                if (currentOtpChars[index] != ' ') {
                                    currentOtpChars[index] = ' '
                                    viewModel.updateOTPCode(currentOtpChars.joinToString("").replace(" ", "").trim())
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
                    if (otpCode.length == 4) {
                        viewModel.verifyOTP()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && otpCode.length == 4
            ) {
                if (isLoading) {
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
                    onClick = { viewModel.sendOTP() },
                    modifier = Modifier.size(140.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
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

            successMessage?.let { message ->
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
