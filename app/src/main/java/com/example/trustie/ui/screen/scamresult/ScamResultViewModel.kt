
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

    private val _isTtsLoading = MutableStateFlow(false)
    val isTtsLoading: StateFlow<Boolean> = _isTtsLoading.asStateFlow()

    private val _audioFilePath = MutableStateFlow<String?>(null)
    val audioFilePath: StateFlow<String?> = _audioFilePath.asStateFlow()

    // Cache for TTS audio files: text hash -> audio file path
    private val _ttsCache = MutableStateFlow<Map<String, String>>(emptyMap())
    val ttsCache: StateFlow<Map<String, String>> = _ttsCache.asStateFlow()

    // Get verification response from GlobalStateManager
    val scamResultData: StateFlow<ScamResultData?> = globalStateManager.scamResultData

    private fun generateTextHash(text: String): String {
        return text.hashCode().toString()
    }

    fun speakContent(contentType: TtsContentType = TtsContentType.FULL_ANALYSIS) {
        viewModelScope.launch {
            _isTtsLoading.value = true
            _isSpeaking.value = false
            try {
                val result = scamResultData.value
                if (result != null) {
                    val speechText = when (result) {
                        is ScamResultData.ImageVerification -> buildSpeechTextByType(result.data, contentType)
                        is ScamResultData.ScamAnalysis -> buildSpeechTextByType(result.data, contentType)
                    }
                    
                    val textHash = generateTextHash(speechText)
                    
                    // Check if audio is already cached
                    val cachedAudioPath = _ttsCache.value[textHash]
                    if (cachedAudioPath != null) {
                        Log.d("ScamResultViewModel", "Using cached audio for $contentType: $cachedAudioPath")
                        _audioFilePath.value = cachedAudioPath
                        _isTtsLoading.value = false
                        _isSpeaking.value = true
                        
                        audioPlayer.playAudioFile(cachedAudioPath) {
                            _isSpeaking.value = false
                            Log.d("ScamResultViewModel", "Audio playback completed")
                        }
                        return@launch
                    }
                    
                    Log.d("ScamResultViewModel", "Starting TTS for $contentType, text length: ${speechText.length}")
                    val audioFilePath = textToSpeechRepository.textToSpeech(speechText).getOrThrow()
                    _audioFilePath.value = audioFilePath
                    
                    // Cache the audio file
                    _ttsCache.value = _ttsCache.value + (textHash to audioFilePath)
                    
                    Log.d("ScamResultViewModel", "TTS completed, audio file: $audioFilePath")
                    _isTtsLoading.value = false
                    _isSpeaking.value = true
                    
                    audioPlayer.playAudioFile(audioFilePath) {
                        _isSpeaking.value = false
                        Log.d("ScamResultViewModel", "Audio playback completed")
                    }
                } else {
                    _isTtsLoading.value = false
                    _isSpeaking.value = false
                }
            } catch (e: Exception) {
                Log.e("ScamResultViewModel", "Error speaking $contentType", e)
                _isTtsLoading.value = false
                _isSpeaking.value = false
            }
        }
    }

    enum class TtsContentType {
        FULL_ANALYSIS,    // Complete analysis with all details
        RECOMMENDATIONS,  // Only key recommendations and safety tips
        SUMMARY          // Brief summary of results
    }

    private fun buildSpeechTextByType(response: ScamAnalysisResponse, contentType: TtsContentType): String {
        return when (contentType) {
            TtsContentType.FULL_ANALYSIS -> buildSpeechText(response)
            TtsContentType.RECOMMENDATIONS -> buildRecommendationsText(response)
            TtsContentType.SUMMARY -> buildSummaryText(response)
        }
    }

    private fun buildSpeechTextByType(response: ImageVerificationResponse, contentType: TtsContentType): String {
        return when (contentType) {
            TtsContentType.FULL_ANALYSIS -> buildSpeechText(response)
            TtsContentType.RECOMMENDATIONS -> buildRecommendationsText(response)
            TtsContentType.SUMMARY -> buildSummaryText(response)
        }
    }

    // Convenience methods for backward compatibility and easier usage
    fun speakAnalysis() = speakContent(TtsContentType.FULL_ANALYSIS)
    fun speakRecommendations() = speakContent(TtsContentType.RECOMMENDATIONS)
    fun speakSummary() = speakContent(TtsContentType.SUMMARY)

    private fun buildSpeechText(response: ScamAnalysisResponse): String {
        return buildString {
            append("Kết quả phân tích: ")
            when (response.risk_level.uppercase()) {
                "HIGH" -> append("Mức độ nguy hiểm cao. ")
                "MEDIUM" -> append("Mức độ nghi ngờ trung bình. ")
                "LOW" -> append("Mức độ an toàn. ")
            }
            append("Độ tin cậy: ${response.confidence} phần trăm. ")
            
            // Add detailed analysis
            if (response.analysis.isNotEmpty()) {
                append("Phân tích chi tiết: ${response.analysis} ")
            }
            
            // Add recommendation with emphasis
            if (response.recommendation.isNotEmpty()) {
                append("Khuyến nghị quan trọng: ${response.recommendation} ")
            }
            
            // Add additional safety tips based on risk level
            append("Lời khuyên bổ sung: ")
            when (response.risk_level.uppercase()) {
                "HIGH" -> append("Hãy cẩn thận tuyệt đối, không thực hiện bất kỳ hành động nào được yêu cầu. Liên hệ ngay với cơ quan chức năng nếu cần thiết. ")
                "MEDIUM" -> append("Hãy thận trọng và xác minh thông tin từ nhiều nguồn khác nhau trước khi đưa ra quyết định. ")
                "LOW" -> append("Nội dung này có vẻ an toàn, nhưng vẫn nên duy trì sự cảnh giác. ")
            }
        }
    }

    private fun buildSpeechText(response: ImageVerificationResponse): String {
        return buildString {
            append("Kết quả phân tích: ")

            val effectiveRiskLevel = response.getEffectiveRiskLevel()

            // Add risk level with clear emphasis
            append("Mức độ rủi ro: ")
            when (effectiveRiskLevel.uppercase()) {
                "HIGH" -> append("Cao - Cần cảnh báo ngay lập tức. ")
                "MEDIUM" -> append("Trung bình - Cần thận trọng. ")
                "LOW" -> append("Thấp - Tương đối an toàn. ")
                else -> append("Không xác định - Cần kiểm tra thêm. ")
            }

            // Add effective confidence
            response.getEffectiveConfidence()?.let { confidence ->
                append("Độ tin cậy của phân tích: $confidence phần trăm. ")
            }

            // Add readable analysis
            val readableAnalysis = response.getReadableAnalysis()
            if (readableAnalysis.isNotEmpty() && readableAnalysis != "Không có thông tin phân tích") {
                append("Phân tích chi tiết: $readableAnalysis ")
            }

            // Add recommendations with clear structure
            val recommendations = response.getRecommendations()
            if(recommendations.isNotEmpty())  append("Khuyến nghị: $recommendations ")


            // Add entities information if available
            response.entities?.let { entities ->
                var hasEntities = false
                
                entities.phones?.takeIf { it.isNotEmpty() }?.let { phones ->
                    if (!hasEntities) {
                        append("Thông tin phát hiện: ")
                        hasEntities = true
                    }
                    append("Số điện thoại: ${phones.joinToString(", ")}. ")
                }
                entities.emails?.takeIf { it.isNotEmpty() }?.let { emails ->
                    if (!hasEntities) {
                        append("Thông tin phát hiện: ")
                        hasEntities = true
                    }
                    append("Email: ${emails.joinToString(", ")}. ")
                }
                entities.urls?.takeIf { it.isNotEmpty() }?.let { urls ->
                    if (!hasEntities) {
                        append("Thông tin phát hiện: ")
                        hasEntities = true
                    }
                    append("Đường link: ${urls.joinToString(", ")}. ")
                }
            }

            // Add final safety reminder based on risk level
            append("Lời nhắc cuối cùng: ")
            when (effectiveRiskLevel.uppercase()) {
                "HIGH" -> append("Nội dung này có nguy cơ cao, hãy tránh xa và báo cáo ngay lập tức. ")
                "MEDIUM" -> append("Hãy thận trọng với nội dung này và xác minh thông tin kỹ lưỡng. ")
                "LOW" -> append("Nội dung này có vẻ an toàn, nhưng vẫn nên duy trì sự cảnh giác. ")
                else -> append("Cần thêm thông tin để đánh giá chính xác mức độ rủi ro. ")
            }
        }
    }

    private fun buildRecommendationsText(response: ScamAnalysisResponse): String {
        return buildString {
            append("Khuyến nghị chính: ")
            if (response.recommendation.isNotEmpty()) {
                append(response.recommendation)
            } else {
                append("Không có khuyến nghị cụ thể. ")
            }
            
            append("Lời khuyên bổ sung: ")
            when (response.risk_level.uppercase()) {
                "HIGH" -> append("Hãy cẩn thận tuyệt đối, không thực hiện bất kỳ hành động nào được yêu cầu. Liên hệ ngay với cơ quan chức năng nếu cần thiết. ")
                "MEDIUM" -> append("Hãy thận trọng và xác minh thông tin từ nhiều nguồn khác nhau trước khi đưa ra quyết định. ")
                "LOW" -> append("Nội dung này có vẻ an toàn, nhưng vẫn nên duy trì sự cảnh giác. ")
            }
        }
    }

    private fun buildRecommendationsText(response: ImageVerificationResponse): String {
        return buildString {
            append("Khuyến nghị chính: ")
            val recommendations = response.getRecommendations()
            if (recommendations.isNotEmpty()) {
                recommendations.forEachIndexed { index, recommendation ->
                    append("Khuyến nghị ${index + 1}: $recommendation. ")
                }
            } else {
                append("Không có khuyến nghị cụ thể. ")
            }
            
            append("Lời nhắc cuối cùng: ")
            val effectiveRiskLevel = response.getEffectiveRiskLevel()
            when (effectiveRiskLevel.uppercase()) {
                "HIGH" -> append("Nội dung này có nguy cơ cao, hãy tránh xa và báo cáo ngay lập tức. ")
                "MEDIUM" -> append("Hãy thận trọng với nội dung này và xác minh thông tin kỹ lưỡng. ")
                "LOW" -> append("Nội dung này có vẻ an toàn, nhưng vẫn nên duy trì sự cảnh giác. ")
                else -> append("Cần thêm thông tin để đánh giá chính xác mức độ rủi ro. ")
            }
        }
    }

    private fun buildSummaryText(response: ScamAnalysisResponse): String {
        return buildString {
            append("Tóm tắt kết quả: ")
            when (response.risk_level.uppercase()) {
                "HIGH" -> append("Mức độ nguy hiểm cao. ")
                "MEDIUM" -> append("Mức độ nghi ngờ trung bình. ")
                "LOW" -> append("Mức độ an toàn. ")
            }
            append("Độ tin cậy: ${response.confidence} phần trăm. ")

        }
    }

    private fun buildSummaryText(response: ImageVerificationResponse): String {
        return buildString {
            append("Tóm tắt kết quả: ")
            val effectiveRiskLevel = response.getEffectiveRiskLevel()
            when (effectiveRiskLevel.uppercase()) {
                "HIGH" -> append("Mức độ rủi ro cao. ")
                "MEDIUM" -> append("Mức độ rủi ro trung bình. ")
                "LOW" -> append("Mức độ rủi ro thấp. ")
                else -> append("Mức độ rủi ro không xác định. ")
            }
            
            response.getEffectiveConfidence()?.let { confidence ->
                append("Độ tin cậy: $confidence phần trăm. ")
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
        _isTtsLoading.value = false
    }

    fun cancelTts() {
        audioPlayer.stopAudio()
        _isTtsLoading.value = false
        _isSpeaking.value = false
        Log.d("ScamResultViewModel", "TTS operation cancelled")
    }

    fun clearTtsCache() {
        _ttsCache.value = emptyMap()
        Log.d("ScamResultViewModel", "TTS cache cleared")
    }

    fun getTtsCacheStats(): String {
        val cacheSize = _ttsCache.value.size
        val totalSize = _ttsCache.value.values.sumOf { filePath ->
            try {
                java.io.File(filePath).length()
            } catch (e: Exception) {
                0L
            }
        }
        return "Cache entries: $cacheSize, Total size: ${totalSize / 1024} KB"
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
