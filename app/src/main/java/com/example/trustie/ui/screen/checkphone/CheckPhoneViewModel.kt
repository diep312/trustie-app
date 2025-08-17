package com.example.trustie.ui.screen.checkphone

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.GlobalStateManager
import com.example.trustie.data.model.PhoneCheckItem
import com.example.trustie.data.model.response.PhoneCheckResponse
import com.example.trustie.repository.phonerepo.PhoneRepository
import com.example.trustie.repository.ttsrepo.TextToSpeechRepository
import com.example.trustie.utils.AudioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckPhoneViewModel @Inject constructor(
    private val repository: PhoneRepository,
    private val globalStateManager: GlobalStateManager,
    private val textToSpeechRepository: TextToSpeechRepository,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _checkResult = MutableStateFlow<PhoneCheckItem?>(null)
    val checkResult: StateFlow<PhoneCheckItem?> = _checkResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isTtsLoading = MutableStateFlow(false)
    val isTtsLoading: StateFlow<Boolean> = _isTtsLoading.asStateFlow()

    private val _audioFilePath = MutableStateFlow<String?>(null)
    val audioFilePath: StateFlow<String?> = _audioFilePath.asStateFlow()

    // Cache for TTS audio files: text hash -> audio file path
    private val _ttsCache = MutableStateFlow<Map<String, String>>(emptyMap())
    val ttsCache: StateFlow<Map<String, String>> = _ttsCache.asStateFlow()

    init {
        Log.d("CheckPhoneDebug", "CheckPhoneViewModel initialized")
    }

    private fun generateTextHash(text: String): String {
        return text.hashCode().toString()
    }

    fun updatePhoneNumber(number: String) {
        _phoneNumber.value = number
        // Clear previous results when phone number changes
        if (_checkResult.value != null) {
            _checkResult.value = null
            _errorMessage.value = null
        }
    }

    fun checkPhoneNumber() {
        val number = _phoneNumber.value.trim()
        if (number.isEmpty()) {
            _errorMessage.value = "Vui lòng nhập số điện thoại"
            return
        }

        if (!isValidPhoneNumber(number)) {
            _errorMessage.value = "Số điện thoại không hợp lệ"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Get user ID from global state
                val userId = globalStateManager.getUserId()
                Log.d("CheckPhoneDebug", "Checking phone number: $number with userId: $userId")
                
                val response = repository.checkPhoneNumber(number, userId)

                // Convert PhoneCheckResponse to PhoneCheckItem
                val phoneCheckItem = PhoneCheckItem(
                    phoneNumber = number,
                    isSuspicious = response.isFlagged,
                    riskLevel = if (response.isFlagged) "High" else "Low",
                    reportCount = 0,
                    lastReported = null,
                    description = response.flagReason
                )
                
                _checkResult.value = phoneCheckItem
                Log.d("CheckPhoneDebug", "Phone check successful: $phoneCheckItem")
                
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
                Log.e("CheckPhoneDebug", "Exception during phone check: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearResults() {
        _checkResult.value = null
        _errorMessage.value = null
    }

    private fun isValidPhoneNumber(number: String): Boolean {
        val cleanNumber = number.replace("\\s".toRegex(), "")
        return cleanNumber.matches(Regex("^(\\+84|84|0)[3-9][0-9]{8}$"))
    }

    fun startVoiceInput() {
        Log.d("CheckPhoneDebug", "Voice input requested")
        _errorMessage.value = "Tính năng nhập bằng giọng nói đang được phát triển"
    }

    fun speakContent(contentType: TtsContentType = TtsContentType.FULL_RESULT) {
        viewModelScope.launch {
            _isTtsLoading.value = true
            _isSpeaking.value = false
            try {
                val result = _checkResult.value
                if (result != null) {
                    val speechText = buildSpeechTextByType(result, contentType)
                    
                    val textHash = generateTextHash(speechText)
                    
                    // Check if audio is already cached
                    val cachedAudioPath = _ttsCache.value[textHash]
                    if (cachedAudioPath != null) {
                        Log.d("CheckPhoneDebug", "Using cached audio for $contentType: $cachedAudioPath")
                        _audioFilePath.value = cachedAudioPath
                        _isTtsLoading.value = false
                        _isSpeaking.value = true
                        
                        audioPlayer.playAudioFile(cachedAudioPath) {
                            _isSpeaking.value = false
                            Log.d("CheckPhoneDebug", "Audio playback completed")
                        }
                        return@launch
                    }
                    
                    Log.d("CheckPhoneDebug", "Starting TTS for $contentType, text length: ${speechText.length}")
                    val audioFilePath = textToSpeechRepository.textToSpeech(speechText).getOrThrow()
                    _audioFilePath.value = audioFilePath
                    
                    // Cache the audio file
                    _ttsCache.value = _ttsCache.value + (textHash to audioFilePath)
                    
                    Log.d("CheckPhoneDebug", "TTS completed, audio file: $audioFilePath")
                    _isTtsLoading.value = false
                    _isSpeaking.value = true
                    
                    audioPlayer.playAudioFile(audioFilePath) {
                        _isSpeaking.value = false
                        Log.d("CheckPhoneDebug", "Audio playback completed")
                    }
                } else {
                    _isTtsLoading.value = false
                    _isSpeaking.value = false
                }
            } catch (e: Exception) {
                Log.e("CheckPhoneDebug", "Error speaking $contentType", e)
                _isTtsLoading.value = false
                _isSpeaking.value = false
            }
        }
    }

    enum class TtsContentType {
        FULL_RESULT,      // Complete phone check result with analysis
        RECOMMENDATIONS   // Only key recommendations and safety tips
    }

    private fun buildSpeechTextByType(result: PhoneCheckItem, contentType: TtsContentType): String {
        return when (contentType) {
            TtsContentType.FULL_RESULT -> buildPhoneCheckSpeechText(result)
            TtsContentType.RECOMMENDATIONS -> buildRecommendationsText(result)
        }
    }

    // Convenience methods for backward compatibility and easier usage
    fun speakPhoneCheckResult() = speakContent(TtsContentType.FULL_RESULT)
    fun speakRecommendations() = speakContent(TtsContentType.RECOMMENDATIONS)

    private fun buildPhoneCheckSpeechText(result: PhoneCheckItem): String {
        return buildString {
            append("Kết quả kiểm tra số điện thoại: ")
            append("Số ${result.phoneNumber}. ")
            
            if (result.isSuspicious) {
                append("Số điện thoại này có dấu hiệu đáng ngờ. ")
                append("Mức độ rủi ro: Cao. ")
                if (result.description?.isNotEmpty() == true) {
                    append("Lý do: ${result.description}. ")
                }
                append("Khuyến nghị: Không nên liên lạc với số này. Hãy báo cáo nếu cần thiết. ")
            } else {
                append("Số điện thoại này có vẻ an toàn. ")
                append("Mức độ rủi ro: Thấp. ")
                append("Khuyến nghị: Có thể liên lạc bình thường, nhưng vẫn nên duy trì sự cảnh giác. ")
            }
        }
    }

    private fun buildRecommendationsText(result: PhoneCheckItem): String {
        return buildString {
            append("Khuyến nghị cho số ${result.phoneNumber}: ")
            
            if (result.isSuspicious) {
                append("Khuyến nghị chính: Không nên liên lạc với số này. ")
                if (result.description?.isNotEmpty() == true) {
                    append("Lý do cảnh báo: ${result.description}. ")
                }
                append("Hành động cần thiết: Hãy chặn số này và báo cáo nếu cần thiết. ")
                append("Lời khuyên bổ sung: Luôn cảnh giác với các cuộc gọi từ số lạ, đặc biệt khi họ yêu cầu thông tin cá nhân hoặc tài chính. ")
            } else {
                append("Khuyến nghị chính: Có thể liên lạc bình thường. ")
                append("Lời khuyên bổ sung: Mặc dù số này có vẻ an toàn, vẫn nên duy trì sự cảnh giác và không chia sẻ thông tin nhạy cảm qua điện thoại. ")
            }
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
        Log.d("CheckPhoneDebug", "TTS operation cancelled")
    }

    fun clearTtsCache() {
        _ttsCache.value = emptyMap()
        Log.d("CheckPhoneDebug", "TTS cache cleared")
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
}