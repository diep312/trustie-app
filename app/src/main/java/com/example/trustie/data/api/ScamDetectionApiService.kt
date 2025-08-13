package com.example.trustie.data.api

import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.POST
import retrofit2.http.Part

interface ScamDetectionApiService {
    @POST("scam-detection/audio-script-assessment/")
    suspend fun analyzeAudioTranscript(
        @Field("text") transcript: String
    ): Map<String, Any>
}