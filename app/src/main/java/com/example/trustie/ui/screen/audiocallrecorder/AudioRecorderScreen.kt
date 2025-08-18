package com.example.trustie.ui.screen.audiocallrecorder

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trustie.data.GlobalStateManager
import com.example.trustie.data.model.response.ImageVerificationResponse
import com.example.trustie.data.model.response.ScamAnalysisResponse
import com.example.trustie.ui.components.AnimatedRipples
import com.example.trustie.ui.components.ScreenHeader
import com.example.trustie.ui.screen.scamresult.ScamResultData
import com.example.trustie.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecorderScreen(
    onBackClick: () -> Unit,
    onNavigateToScamResult: (ScamAnalysisResponse) -> Unit,
    viewModel: AudioRecorderViewmodel = hiltViewModel()
) {
    val stableTranscript by viewModel.stableTranscript.observeAsState("")
    val pendingChunk by viewModel.pendingChunk.observeAsState("")
    val scamDetected by viewModel.scamDetected.observeAsState(false)

    val scamResultData by viewModel.globalStateManager.scamResultData.collectAsState()

    // Track recording state
    var isRecording by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Log.d("AudioRecorderScreen", "Screen entered, resetting to initial state")
        viewModel.resetScamDetection()
    }

    LaunchedEffect(scamResultData) {
        if (scamResultData is ScamResultData.ScamAnalysis) {
            Log.d("AudioRecorderScreen", "Scam data detected, navigating to result screen")
            onNavigateToScamResult((scamResultData as ScamResultData.ScamAnalysis).data)
        }
    }

    // Determine if there's any transcript to show
    val hasTranscript = stableTranscript.isNotEmpty() || pendingChunk.isNotEmpty()

    val displayText = if (hasTranscript) {
        buildAnnotatedString {
            if (stableTranscript.isNotEmpty()) {
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append(stableTranscript)
                }
            }
            if (pendingChunk.isNotBlank()) {
                if (stableTranscript.isNotEmpty()) append(" ")
                withStyle(style = SpanStyle(color = Color.Gray.copy(alpha = 0.7f))) {
                    append(pendingChunk)
                }
            }
        }
    } else {
        buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Gray)) {
                append("Ghi âm cuộc gọi, nếu có dấu hiệu lừa đảo sẽ cảnh cáo")
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFFDF2E9)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFDF2E9))
        ) {
            ScreenHeader(
                title = "Ghi âm",
                onBackClick = onBackClick,
                backgroundColor = Color(0xFFFDF2E9),
                titleColor = Color.Black,
                returnText = "Quay về"
            )

            // Main content area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Scam detection warning (if detected)
                if (scamDetected) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Red),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚠️",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "Có dấu hiệu lừa đảo!",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(0.3f))

                // Recording button with animation
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // Animated ripple effect when recording
                    if (isRecording) {
                        AnimatedRipples()
                    }

                    // Main recording button
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(
                                if (isRecording) Color(0xFF2196F3) else Color.Gray
                            )
                            .clickable {
                                if (isRecording) {
                                    viewModel.stopListening()
                                    isRecording = false
                                } else {
                                    viewModel.startListening()
                                    isRecording = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mic),
                            contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Transcript text
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.weight(0.5f))
            }
        }
    }
}