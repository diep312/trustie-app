package com.example.trustie.repository.audiotranscriptrepo

import androidx.lifecycle.LiveData


 interface AudioTranscriptRepository {
    fun startListening()

    fun stopListening()

     val stableTranscript: LiveData<String>

     val pendingChunk: LiveData<String>

     val scamDetected: LiveData<Boolean>

     fun resetScamDetection()

}