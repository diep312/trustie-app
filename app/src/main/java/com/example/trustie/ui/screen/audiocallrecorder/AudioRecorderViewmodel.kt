package com.example.trustie.ui.screen.audiocallrecorder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.GlobalStateManager
import com.example.trustie.repository.audiotranscriptrepo.AudioTranscriptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioRecorderViewmodel @Inject constructor(
    private val audioTranscriptRepository: AudioTranscriptRepository,
    val globalStateManager: GlobalStateManager
) : ViewModel() {

    val stableTranscript: LiveData<String> = audioTranscriptRepository.stableTranscript

    val pendingChunk: LiveData<String> = audioTranscriptRepository.pendingChunk
    val scamDetected: LiveData<Boolean> = audioTranscriptRepository.scamDetected

    fun getScamResultData() = globalStateManager.scamResultData.value


    fun startListening() {
        viewModelScope.launch {
            audioTranscriptRepository.startListening()
        }
    }

    fun resetScamDetection() {
        audioTranscriptRepository.resetScamDetection()
    }

    fun stopListening() {
        audioTranscriptRepository.stopListening()
    }
}