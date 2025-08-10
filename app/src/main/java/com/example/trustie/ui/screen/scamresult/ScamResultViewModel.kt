
package com.example.trustie.ui.screen.scamresult

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.GlobalStateManager
import com.example.trustie.data.model.response.ImageVerificationResponse
import com.example.trustie.data.model.response.getReadableAnalysis
import com.example.trustie.data.model.response.getRecommendations
import com.example.trustie.data.model.response.getEffectiveRiskLevel
import com.example.trustie.data.model.response.getEffectiveConfidence
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
        Log.d("ScamResultViewModel", "Set verification response: ${response.getEffectiveRiskLevel()}")
        Log.d("ScamResultViewModel", "Effective confidence: ${response.getEffectiveConfidence()}")
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
                    val speechText = buildSpeechText(response)

                    Log.d("ScamResultViewModel", "Speaking text: $speechText")
                    val audioFilePath = textToSpeechRepository.textToSpeech(speechText).getOrThrow()
                    _audioFilePath.value = audioFilePath

                    // Play the audio file
                    audioPlayer.playAudioFile(audioFilePath) {
                        _isSpeaking.value = false
                    }
                } else {
                    Log.w("ScamResultViewModel", "No verification response available for speech")
                    _isSpeaking.value = false
                }
            } catch (e: Exception) {
                Log.e("ScamResultViewModel", "Error speaking analysis", e)
                _isSpeaking.value = false
            }
        }
    }

    private fun buildSpeechText(response: ImageVerificationResponse): String {
        return buildString {
            append("Kết quả phân tích: ")

            val effectiveRiskLevel = response.getEffectiveRiskLevel()

            // Add risk level
            when (effectiveRiskLevel.uppercase()) {
                "HIGH" -> append("Mức độ nguy hiểm cao. ")
                "MEDIUM" -> append("Mức độ nghi ngờ trung bình. ")
                "LOW" -> append("Mức độ an toàn. ")
                else -> append("Mức độ an toàn. ")
            }

            // Add effective confidence
            response.getEffectiveConfidence()?.let { confidence ->
                append("Độ tin cậy: $confidence phần trăm. ")
            }

            // Add readable analysis
            val readableAnalysis = response.getReadableAnalysis()
            if (readableAnalysis.isNotEmpty() && readableAnalysis != "Không có thông tin phân tích") {
                append("Phân tích chi tiết: $readableAnalysis ")
            }

            // Add recommendations
            val recommendations = response.getRecommendations()
            if (recommendations.isNotEmpty()) {
                append("Khuyến nghị: ")
                recommendations.forEachIndexed { index, recommendation ->
                    append("${index + 1}. $recommendation. ")
                }
            }

            // Add OCR text if available
            response.ocrText?.let { ocrText ->
                if (ocrText.isNotEmpty()) {
                    append("Văn bản được phát hiện: $ocrText ")
                }
            }

            // Add entities information if available
            response.entities?.let { entities ->
                entities.phones?.takeIf { it.isNotEmpty() }?.let { phones ->
                    append("Số điện thoại phát hiện: ${phones.joinToString(", ")}. ")
                }
                entities.emails?.takeIf { it.isNotEmpty() }?.let { emails ->
                    append("Email phát hiện: ${emails.joinToString(", ")}. ")
                }
                entities.urls?.takeIf { it.isNotEmpty() }?.let { urls ->
                    append("Đường link phát hiện: ${urls.joinToString(", ")}. ")
                }
            }
        }
    }

    fun contactRelatives() {
        viewModelScope.launch {
            try {
                val response = verificationResponse.value
                if (response != null) {
                    val message = buildContactMessage(response)
                    Log.d("ScamResultViewModel", "Contacting relatives with message: $message")
                    // TODO: Implement actual contact functionality
                    // This could involve sending SMS, making calls, or opening contact app
                } else {
                    Log.w("ScamResultViewModel", "No verification response available for contact")
                }
            } catch (e: Exception) {
                Log.e("ScamResultViewModel", "Error contacting relatives", e)
            }
        }
    }

    private fun buildContactMessage(response: ImageVerificationResponse): String {
        val effectiveRiskLevel = response.getEffectiveRiskLevel()

        return buildString {
            append("Cảnh báo từ ứng dụng Trustie: ")
            when (effectiveRiskLevel.uppercase()) {
                "HIGH" -> append("Phát hiện nội dung lừa đảo nguy hiểm. ")
                "MEDIUM" -> append("Phát hiện nội dung nghi ngờ. ")
                "LOW" -> append("Nội dung được đánh giá an toàn. ")
                else -> append("Nội dung được đánh giá an toàn. ")
            }
            append("Vui lòng liên hệ để được hỗ trợ thêm.")
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
