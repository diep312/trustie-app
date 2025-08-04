package com.example.trustie.ui.screen.scamresult

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Stop
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
import com.example.trustie.R
import com.example.trustie.data.model.response.ImageVerificationResponse
import com.example.trustie.ui.components.ScreenHeader
import com.example.trustie.ui.theme.TrustieTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScamResultScreen(
    onBackClick: () -> Unit,
    viewModel: ScamResultViewModel = hiltViewModel()
) {
    val verificationResponse by viewModel.verificationResponse.collectAsState()
    val isHighRisk = verificationResponse?.llmAnalysis?.riskLevel?.uppercase() == "HIGH"
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val isAudioPlaying by remember { derivedStateOf { viewModel.isAudioPlaying() } }
    
    // Handle back navigation - clear the verification response when going back
    val handleBackClick = {
        viewModel.clearVerificationResponse()
        onBackClick()
    }

    Scaffold{padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            ScreenHeader(
                title = "Kết quả",
                onBackClick = handleBackClick
            )

            if (isHighRisk) {
                WarningContent(
                    onAiAnalysisClick = { viewModel.speakAnalysis() },
                    onContactRelativesClick = { viewModel.contactRelatives() },
                    onStopAudio = { viewModel.stopAudio() },
                    onPauseAudio = { viewModel.pauseAudio() },
                    onResumeAudio = { viewModel.resumeAudio() },
                    isSpeaking = isSpeaking,
                    isAudioPlaying = isAudioPlaying,
                    verificationResponse = verificationResponse
                )
            } else {
                SafeContent(
                    onAiAnalysisClick = { viewModel.speakAnalysis() },
                    onContactRelativesClick = { viewModel.contactRelatives() },
                    onStopAudio = { viewModel.stopAudio() },
                    onPauseAudio = { viewModel.pauseAudio() },
                    onResumeAudio = { viewModel.resumeAudio() },
                    isSpeaking = isSpeaking,
                    isAudioPlaying = isAudioPlaying,
                    verificationResponse = verificationResponse
                )
            }
        }
    }
}

@Composable
private fun WarningContent(
    onAiAnalysisClick: () -> Unit,
    onContactRelativesClick: () -> Unit,
    onStopAudio: () -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    isSpeaking: Boolean,
    isAudioPlaying: Boolean,
    verificationResponse: ImageVerificationResponse?
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Warning title
        Card(
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = "Cảnh báo!",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFD32F2F),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // Warning logo
            Image(
                painter = painterResource(id = R.drawable.img_warning_logo),
                contentDescription = "Warning logo",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )

            // Description
            Text(
                text = "Hệ thống phát hiện nội dung lừa đảo",
                fontSize = 18.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // AI Analysis button
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = if (isSpeaking) onStopAudio else onAiAnalysisClick,
                    modifier = Modifier.size(140.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSpeaking) Color(0xFFD32F2F) else Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.QuestionMark,
                        contentDescription = if (isSpeaking) "Stop Audio" else "AI Analysis",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )

                    Text(
                        text = if (isSpeaking) "Dừng phát" else "AI phân tích",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }

            // Contact Relatives button
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onContactRelativesClick,
                    modifier = Modifier.size(140.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0)
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_people),
                        contentDescription = "Contact Relatives",
                        modifier = Modifier.size(80.dp)
                    )

                    Text(
                        text = "Liên hệ người thân",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun SafeContent(
    onAiAnalysisClick: () -> Unit,
    onContactRelativesClick: () -> Unit,
    onStopAudio: () -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    isSpeaking: Boolean,
    isAudioPlaying: Boolean,
    verificationResponse: ImageVerificationResponse?
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Safe title
        Card(
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = "An toàn!",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // Safe logo
            Image(
                painter = painterResource(id = R.drawable.img_safe),
                contentDescription = "Safe logo",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )

            // Description
            Text(
                text = "Hệ thống không phát hiện\nnội dung lừa đảo",
                fontSize = 18.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }


        Spacer(modifier = Modifier.weight(1f))

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // AI Analysis button
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = if (isSpeaking) onStopAudio else onAiAnalysisClick,
                    modifier = Modifier.size(140.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSpeaking) Color(0xFFD32F2F) else Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.QuestionMark,
                        contentDescription = if (isSpeaking) "Stop Audio" else "AI Analysis",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )

                    Text(
                        text = if (isSpeaking) "Dừng phát" else "AI phân tích",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }

            }

            // Contact Relatives button
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onContactRelativesClick,
                    modifier = Modifier.size(140.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0)
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_people),
                        contentDescription = "Contact Relatives",
                        modifier = Modifier.size(80.dp)
                    )

                    Text(
                        text = "Liên hệ người thân",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )

                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScamResultScreenPreview() {
    TrustieTheme {
        ScamResultScreen(
            onBackClick = {},
        )
    }
} 