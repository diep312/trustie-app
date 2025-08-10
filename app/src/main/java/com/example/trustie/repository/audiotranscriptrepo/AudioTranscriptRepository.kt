package com.example.trustie.repository.audiotranscriptrepo

import androidx.lifecycle.LiveData


 interface AudioTranscriptRepository {
    fun startListening()

    fun stopListening()

    val transcript: LiveData<String>

    val scamDetected: LiveData<Boolean>

}