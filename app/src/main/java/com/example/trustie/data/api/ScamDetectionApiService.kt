package com.example.trustie.data.api

import com.example.trustie.data.model.response.ScamAnalysisResponse
import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Part

interface ScamDetectionApiService {
    @FormUrlEncoded
    @POST("scam-detection/audio-script-assessment/")
    suspend fun analyzeAudioTranscript(
        @Field("transcript") transcript: String
    ): ScamAnalysisResponse
}