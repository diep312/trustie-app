package com.example.trustie.ui.screen.audiocallrecorder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trustie.ui.components.ScreenHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecorderScreen(
    onBackClick: () -> Unit,
    viewModel: AudioRecorderViewmodel = hiltViewModel()
) {
    val stableTranscript by viewModel.stableTranscript.observeAsState("")
    val pendingChunk by viewModel.pendingChunk.observeAsState("")
    val scamDetected by viewModel.scamDetected.observeAsState(false)

    val displayText = buildAnnotatedString {
        if (stableTranscript.isEmpty()) {
            withStyle(style = SpanStyle(color = Color.Gray)) {
                append("Đang chờ ghi âm...")
            }
        } else {
            withStyle(style = SpanStyle(color = Color.Black)) {
                append(stableTranscript)
            }
            if (pendingChunk.isNotBlank()) {
                append(" ")
                withStyle(style = SpanStyle(color = Color.Gray.copy(alpha = 0.5f))) {
                    append(pendingChunk)
                }
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

            Spacer(modifier = Modifier.height(16.dp))

            if (scamDetected) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "⚠ Có dấu hiệu lừa đảo!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.medium)
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { viewModel.startListening() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text("Bắt đầu")
                }
                Button(
                    onClick = { viewModel.stopListening() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Dừng")
                }
            }
        }
    }
}