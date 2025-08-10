
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
import com.example.trustie.data.model.response.ImageVerificationResponse
import com.example.trustie.ui.components.ScreenHeader
import com.example.trustie.ui.theme.TrustieTheme
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageVerificationScreen(
    onBackClick: () -> Unit,
    onNavigateToScamResult: (ImageVerificationResponse) -> Unit,
    viewModel: ImageVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Reset to initial when first entering this screen (only once)
    LaunchedEffect(Unit) {
        Log.d("ImageVerificationScreen", "Screen entered, resetting to initial state")
        viewModel.resetToInitial()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            Log.d("ImageVerificationScreen", "Image selected: $uri")
            viewModel.selectImage(uri)
        }
    )

    // Simple navigation logic - only trigger when we have a response
    LaunchedEffect(uiState.verificationResponse) {
        uiState.verificationResponse?.let { response ->
            Log.d("ImageVerificationScreen", "Got response, navigating immediately")
            onNavigateToScamResult(response)
            // Clear immediately to prevent re-navigation
            viewModel.clearResponseAfterNavigation()
        }
    }

    // Handle back button - DON'T reset here, just go back
    val handleBackClick = {
        Log.d("ImageVerificationScreen", "Back button pressed")
        onBackClick()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDF2E9))
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            ScreenHeader(
                title = "Kiểm tra",
                onBackClick = handleBackClick
            )

            // Show content based on loading state, not verification state
            if (uiState.isLoading) {
                LoadingContent()
            } else {
                InitialUploadContent(
                    onImageSelect = {
                        Log.d("ImageVerificationScreen", "Image select button clicked")
                        imagePickerLauncher.launch("image/*")
                    },
                    onVerifyClick = {
                        Log.d("ImageVerificationScreen", "Verify button clicked")
                        if (uiState.selectedImageUri != null) {
                            viewModel.verifyImage()
                        } else {
                            Log.w("ImageVerificationScreen", "No image selected")
                        }
                    },
                    onGuideClick = { viewModel.showGuide() },
                    selectedImageUri = uiState.selectedImageUri,
                    errorMessage = uiState.errorMessage
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
    selectedImageUri: String?,
    errorMessage: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Chọn ảnh",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        )

        // Error message if any
        errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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

        // Image selection area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 16.dp)
                .border(
                    width = 2.dp,
                    color = Color(0xFF2196F3),
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Plus icon circle
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = Color(0xFF2196F3),
                                shape = RoundedCornerShape(40.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_circle),
                            contentDescription = "Add image",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Bấm vào đây",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2196F3),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "để tải ảnh lên",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2196F3),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Send report button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = onVerifyClick,
                containerColor = if (selectedImageUri != null) Color(0xFF2196F3) else Color.Gray,
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
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
        ImageVerificationScreen(
            onBackClick = {},
            onNavigateToScamResult = {}
        )
    }
}

