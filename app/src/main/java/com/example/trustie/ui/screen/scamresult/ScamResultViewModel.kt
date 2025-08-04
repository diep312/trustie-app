package com.example.trustie.ui.screen.scamresult

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.GlobalStateManager
import com.example.trustie.data.model.response.ImageVerificationResponse
import com.example.trustie.repository.ttsrepo.TextToSpeechRepository
import com.example.trustie.utils.AudioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScamResultViewModel @Inject constructor(
    private val textToSpeechRepository: TextToSpeechRepository,
    private val audioPlayer: AudioPlayer,
    private val globalStateManager: GlobalStateManager
) : ViewModel() {
    
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()
    
    private val _audioFilePath = MutableStateFlow<String?>(null)
    val audioFilePath: StateFlow<String?> = _audioFilePath.asStateFlow()
    
    // Get verification response from GlobalStateManager
    val verificationResponse: StateFlow<ImageVerificationResponse?> = globalStateManager.verificationResponse

    fun setVerificationResponse(response: ImageVerificationResponse) {
        globalStateManager.setVerificationResponse(response)
        Log.d("ScamResultViewModel", "Set verification response: ${response.llmAnalysis.riskLevel}")
    }
    
    fun clearVerificationResponse() {
        globalStateManager.clearVerificationResponse()
        Log.d("ScamResultViewModel", "Cleared verification response")
    }

    fun speakAnalysis() {
        viewModelScope.launch {
            _isSpeaking.value = true
            try {
                val response = verificationResponse.value
                if (response != null) {
                    // Create a comprehensive text for speech
                    val speechText = buildString {
                        append("Kết quả phân tích: ")
                        
                        // Add risk level
                        when (response.llmAnalysis.riskLevel.uppercase()) {
                            "HIGH" -> append("Mức độ nguy hiểm cao. ")
                            "MEDIUM" -> append("Mức độ nghi ngờ trung bình. ")
                            "LOW" -> append("Mức độ an toàn. ")
                            else -> append("Mức độ an toàn. ")
                        }
                        
                        // Add analysis
                        if (response.llmAnalysis.analysis.isNotEmpty()) {
                            append("Phân tích chi tiết: ${response.llmAnalysis.analysis}")
                        }
                        
                        // Add recommendations if available
                        // Note: The current response structure doesn't include recommendations
                        // You might need to extract them from the analysis text or modify the API response
                    }
                    
                    Log.d("ScamResultViewModel", "Speaking text: $speechText")
                    val audioFilePath = textToSpeechRepository.textToSpeech(speechText).getOrThrow()
                    _audioFilePath.value = audioFilePath
                    
                    // Play the audio file
                    audioPlayer.playAudioFile(audioFilePath) {
                        _isSpeaking.value = false
                    }
                }
            } catch (e: Exception) {
                Log.e("ScamResultViewModel", "Error speaking analysis", e)
                _isSpeaking.value = false
            }
        }
    }

    fun contactRelatives() {
        viewModelScope.launch {
            try {
                val response = verificationResponse.value
                if (response != null) {
                    // Create a message for relatives
                    val message = buildString {
                        append("Cảnh báo: ")
                        when (response.llmAnalysis.riskLevel.uppercase()) {
                            "HIGH" -> append("Phát hiện nội dung lừa đảo nguy hiểm. ")
                            "MEDIUM" -> append("Phát hiện nội dung nghi ngờ. ")
                            "LOW" -> append("Nội dung an toàn. ")
                            else -> append("Nội dung an toàn. ")
                        }
                        append("Vui lòng liên hệ để được hỗ trợ.")
                    }
                    
                    Log.d("ScamResultViewModel", "Contacting relatives with message: $message")
                    // Here you would implement the actual contact functionality
                    // For now, we'll just log it
                }
            } catch (e: Exception) {
                Log.e("ScamResultViewModel", "Error contacting relatives", e)
            }
        }
    }
    
    fun stopAudio() {
        audioPlayer.stopAudio()
        _isSpeaking.value = false
    }
    
    fun pauseAudio() {
        audioPlayer.pauseAudio()
    }
    
    fun resumeAudio() {
        audioPlayer.resumeAudio()
    }
    
    fun isAudioPlaying(): Boolean {
        return audioPlayer.isPlaying()
    }
    
    override fun onCleared() {
        super.onCleared()
        audioPlayer.stopAudio()
    }
} 