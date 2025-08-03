package com.example.trustie.ui.screen.imagedetection

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.trustie.R
import com.example.trustie.data.model.VerificationState
import com.example.trustie.ui.theme.TrustieTheme
import com.example.trustie.ui.screen.imagedetection.ImageVerificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageVerificationScreen(
    onBackClick: () -> Unit,
    viewModel: ImageVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            viewModel.selectImage(uri)
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF2E9))
    ) {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.height(50.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "KIỂM TRA",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF208EE1),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(60.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.back_buttonn),
                        contentDescription = "Back",
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFFDF2E9)
            )
        )
        Spacer(modifier = Modifier.height(64.dp))

        when (uiState.verificationState) {
            VerificationState.INITIAL -> {
                InitialUploadContent(
                    onImageSelect = { imagePickerLauncher.launch("image/*") },
                    onVerifyClick = { viewModel.verifyImage() },
                    onGuideClick = { viewModel.showGuide() },
                    selectedImageUri = uiState.selectedImageUri
                )
            }
            VerificationState.LOADING -> {
                LoadingContent()
            }
            VerificationState.WARNING -> {
                WarningContent(
                    onUnderstoodClick = { viewModel.resetToInitial() },
                    onReportClick = { viewModel.reportFraud() },
                    ocrText = uiState.ocrText
                )
            }
            VerificationState.SAFE -> {
                SafeContent(
                    onUnderstoodClick = { viewModel.resetToInitial() },
                    onReportClick = { viewModel.reportSafe() },
                    ocrText = uiState.ocrText
                )
            }
        }
    }
}

@Composable
private fun InitialUploadContent(
    onImageSelect: () -> Unit,
    onVerifyClick: () -> Unit,
    onGuideClick: () -> Unit,
    selectedImageUri: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Chọn ảnh",
            fontSize = 24.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .border(
                    width = 2.dp,
                    color = Color(0xFF2196F3),
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { onImageSelect() },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(Uri.parse(selectedImageUri)),
                    contentDescription = "Ảnh đã chọn",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = "BẤM VÀO ĐÂY ĐỂ TẢI\nẢNH LÊN",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2196F3),
                    lineHeight = 40.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onVerifyClick,
            enabled = selectedImageUri != null,
            modifier = Modifier
                .width(300.dp)
                .height(70.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "KIỂM TRA",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onGuideClick,
            modifier = Modifier
                .width(300.dp)
                .height(70.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF37474F)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "HƯỚNG DẪN",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun WarningContent(
    onUnderstoodClick: () -> Unit,
    onReportClick: () -> Unit,
    ocrText: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CẢNH BÁO !",
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD32F2F),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Hệ thống phát hiện\nnội dung lừa đảo",
            fontSize = 30.sp,
            lineHeight = 36.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        ocrText?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Văn bản phát hiện: $it",
                fontSize = 20.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Cảnh báo",
            tint = Color(0xFFD32F2F),
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(48.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onUnderstoodClick,
                    modifier = Modifier.size(140.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Safe",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Tôi đã hiểu",
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
                    onClick = onReportClick,
                    modifier = Modifier.size(140.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0)
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Report",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Báo người thân",
                    fontSize = 24.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SafeContent(
    onUnderstoodClick: () -> Unit,
    onReportClick: () -> Unit,
    ocrText: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AN TOÀN !",
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Không phát hiện nội\ndung lừa đảo",
            fontSize = 30.sp,
            color = Color.Black,
            lineHeight = 36.sp,
            textAlign = TextAlign.Center
        )
        ocrText?.let { // Hiển thị OCR text nếu có
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Văn bản phát hiện: $it",
                fontSize = 20.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "An toàn",
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(48.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onUnderstoodClick,
                    modifier = Modifier.size(140.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Safe",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Tôi đã hiểu",
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
                    onClick = onReportClick,
                    modifier = Modifier.size(140.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0)
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Report",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Báo người thân",
                    fontSize = 24.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = Color(0xFF2196F3)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Đang kiểm tra...",
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ImageVerificationScreenPreview() {
    TrustieTheme {
        ImageVerificationScreen(onBackClick = {})
    }
}
