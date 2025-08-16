package com.example.trustie.ui.screen.scamresult

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.QuestionMark
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
import com.example.trustie.data.model.response.getEffectiveRiskLevel
import com.example.trustie.data.model.response.getEffectiveConfidence
import com.example.trustie.data.model.response.getReadableAnalysis
import com.example.trustie.data.model.response.getRecommendations
import com.example.trustie.ui.components.ScreenHeader
import com.example.trustie.ui.theme.TrustieTheme
import android.util.Log
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScamResultScreen(
    onBackClick: () -> Unit,
    viewModel: ScamResultViewModel = hiltViewModel()
) {
    // Get response from GlobalStateManager via ViewModel
    val currentResponse by viewModel.scamResultData.collectAsState()

    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val isAudioPlaying by remember { derivedStateOf { viewModel.isAudioPlaying() }}
    var isHighRisk by remember { mutableStateOf(false) }
    var riskLevel = ""
    var confidence: Int? = null
    var readableAnalysis = ""
    var recommendations  = ""

    Log.d("ScamResultScreen", "ScamResultScreen composed. Response: ${currentResponse != null}")

    // If no response available, show loading instead of going back immediately
    if (currentResponse == null) {
        Log.w("ScamResultScreen", "No verification response available, showing loading...")

        // Show loading screen instead of going back
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
                text = "Đang tải kết quả...",
                fontSize = 16.sp,
                color = Color.Black
            )
        }
        return
    }
    else{
        when (currentResponse) {
            is ScamResultData.ImageVerification -> {
                val data = (currentResponse as ScamResultData.ImageVerification).data
                riskLevel = data.getEffectiveRiskLevel().uppercase()
                confidence = data.getEffectiveConfidence()
                readableAnalysis = data.getReadableAnalysis()
                recommendations = data.getRecommendations()
            }
            is ScamResultData.ScamAnalysis -> {
                val data = (currentResponse as ScamResultData.ScamAnalysis).data
                riskLevel = data.risk_level.uppercase()
                confidence = data.confidence
                readableAnalysis = data.analysis ?: ""
                recommendations = data.recommendation ?: ""
            }

            null -> TODO()
        }

        isHighRisk = riskLevel == "HIGH" || riskLevel == "MEDIUM"
    }



    // Handle back navigation - DON'T clear verification response here
    val handleBackClick = {
        Log.d("ScamResultScreen", "Back button pressed. Going back without clearing response.")
        onBackClick()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize().background(Color(0xFFF8F5F2)) // Light background
                .verticalScroll(rememberScrollState())
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            ScreenHeader(
                title = "Kết quả",
                onBackClick = handleBackClick
            )

            if (isHighRisk) {
                WarningContent(
                    riskLevel = riskLevel,
                    confidence = confidence,
                    readableAnalysis = readableAnalysis,
                    recommendations = recommendations,
                    onAiAnalysisClick = { viewModel.speakAnalysis() },
                    onContactRelativesClick = { viewModel.contactRelatives() },
                    onStopAudio = { viewModel.stopAudio() },
                    onPauseAudio = { viewModel.pauseAudio() },
                    onResumeAudio = { viewModel.resumeAudio() },
                    isSpeaking = isSpeaking,
                    isAudioPlaying = isAudioPlaying
                )
            } else {
                SafeContent(
                    riskLevel = riskLevel,
                    confidence = confidence,
                    onAiAnalysisClick = { viewModel.speakAnalysis() },
                    onContactRelativesClick = { viewModel.contactRelatives() },
                    onStopAudio = { viewModel.stopAudio() },
                    onPauseAudio = { viewModel.pauseAudio() },
                    onResumeAudio = { viewModel.resumeAudio() },
                    isSpeaking = isSpeaking,
                    isAudioPlaying = isAudioPlaying
                )
            }
        }
    }
}

@Composable
private fun WarningContent(
    riskLevel: String,
    confidence: Int?,
    readableAnalysis: String,
    recommendations: String,
    onAiAnalysisClick: () -> Unit,
    onContactRelativesClick: () -> Unit,
    onStopAudio: () -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    isSpeaking: Boolean,
    isAudioPlaying: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFDBD5E8) // your light color
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cảnh báo!",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFD32F2F),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Image(
                    painter = painterResource(id = R.drawable.img_warning_logo),
                    contentDescription = "Warning logo",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Hệ thống phát hiện nội dung lừa đảo",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Mức độ rủi ro: ${riskLevel}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )


                Text(
                    text = "Độ tin cậy: $confidence%",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )

            }
        }

        if (readableAnalysis.isNotEmpty() && readableAnalysis != "Không có thông tin phân tích") {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFDBD5E8) // your light color
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Phân tích chi tiết:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = readableAnalysis,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }


        if (recommendations.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFDBD5E8) // your light color
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Khuyến nghị:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = recommendations,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.QuestionMark,
                            contentDescription = if (isSpeaking) "Stop Audio" else "AI Analysis",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "AI phân tích",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
            }

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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(R.drawable.ic_people),
                            contentDescription = "Contact Relatives",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Liên hệ người thân",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SafeContent(
    riskLevel: String,
    confidence: Int?,
    onAiAnalysisClick: () -> Unit,
    onContactRelativesClick: () -> Unit,
    onStopAudio: () -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    isSpeaking: Boolean,
    isAudioPlaying: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFDBD5E8) // your light color
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "An toàn!",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Image(
                    painter = painterResource(id = R.drawable.img_safe),
                    contentDescription = "Safe logo",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Hệ thống không phát hiện\nnội dung lừa đảo",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Mức độ rủi ro: ${riskLevel}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )


                Text(
                    text = "Độ tin cậy: $confidence%",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.QuestionMark,
                            contentDescription = if (isSpeaking) "Stop Audio" else "AI Analysis",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "AI phân tích",
                            fontSize = 14.sp,

                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
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
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(R.drawable.ic_people),
                            contentDescription = "Contact Relatives",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Liên hệ người thân",
                            fontSize = 14.sp,

                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
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
            onBackClick = {}
        )
    }
}
