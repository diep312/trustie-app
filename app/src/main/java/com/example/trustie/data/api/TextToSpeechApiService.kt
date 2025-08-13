package com.example.trustie.data.api


import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface TextToSpeechApiService {
    
    @FormUrlEncoded
    @POST("text-to-speech/")
    suspend fun textToSpeech(@Field("text") text: String): Response<ResponseBody>
}