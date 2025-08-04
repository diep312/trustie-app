package com.example.trustie.repository.ttsrepo

interface TextToSpeechRepository {
    suspend fun textToSpeech(text: String): Result<String>
    suspend fun downloadAudioFile(audioUrl: String): Result<String>
} 