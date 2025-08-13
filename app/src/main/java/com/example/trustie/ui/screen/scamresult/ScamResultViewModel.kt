
package com.example.trustie.ui.screen.scamresult

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.GlobalStateManager
import com.example.trustie.data.model.response.ImageVerificationResponse
import com.example.trustie.data.model.response.ScamAnalysisResponse
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

    private val _resultData = MutableStateFlow<ScamResultData?>(null)
    val resultData: StateFlow<ScamResultData?> = _resultData.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _audioFilePath = MutableStateFlow<String?>(null)
    val audioFilePath: StateFlow<String?> = _audioFilePath.asStateFlow()

    // Get verification response from GlobalStateManager
    val scamResultData: StateFlow<ScamResultData?> = globalStateManager.scamResultData


    fun speakAnalysis() {
        viewModelScope.launch {
            _isSpeaking.value = true
            try {
                val result = scamResultData.value
                if (result != null) {
                    val speechText = when (result) {
                        is ScamResultData.ImageVerification -> buildSpeechText(result.data)
                        is ScamResultData.ScamAnalysis -> buildSpeechText(result.data)
                    }
                    val audioFilePath = textToSpeechRepository.textToSpeech(speechText).getOrThrow()
                    _audioFilePath.value = audioFilePath
                    audioPlayer.playAudioFile(audioFilePath) {
                        _isSpeaking.value = false
                    }
                } else {
                    _isSpeaking.value = false
                }
            } catch (e: Exception) {
                Log.e("ScamResultViewModel", "Error speaking analysis", e)
                _isSpeaking.value = false
            }
        }
    }



    private fun buildSpeechText(response: ScamAnalysisResponse): String {
        return buildString {
            append("Kết quả phân tích: ")
            when (response.risk_level.uppercase()) {
                "HIGH" -> append("Mức độ nguy hiểm cao. ")
                "MEDIUM" -> append("Mức độ nghi ngờ trung bình. ")
                "LOW" -> append("Mức độ an toàn. ")
            }
            append("Độ tin cậy: ${response.confidence} phần trăm. ")
            append("Phân tích chi tiết: ${response.analysis} ")
            append("Khuyến nghị: ${response.recommendation}")
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
            val result = _resultData.value
            if (result != null) {
                val message = when (result) {
                    is ScamResultData.ImageVerification -> buildContactMessage(result.data.getEffectiveRiskLevel())
                    is ScamResultData.ScamAnalysis -> buildContactMessage(result.data.risk_level)
                }
                Log.d("ScamResultViewModel", "Contact message: $message")
                // TODO: send message via SMS/call
            }
        }
    }

    private fun buildContactMessage(riskLevel: String): String {
        return buildString {
            append("Cảnh báo từ ứng dụng Trustie: ")
            when (riskLevel.uppercase()) {
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
