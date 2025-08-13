package com.example.trustie.repository.ttsrepo

interface TextToSpeechRepository {
    suspend fun textToSpeech(text: String): Result<String>
}