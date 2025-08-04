package com.example.trustie.data.api

import retrofit2.http.*

interface TextToSpeechApiService {
    
    @FormUrlEncoded
    @POST("text-to-speech/")
    suspend fun textToSpeech(@Field("text") text: String): Map<String, Any>
} 