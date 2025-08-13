package com.example.trustie.data.api

/**
 * Centralized API manager that provides access to all API services
 */
object ApiManager {
    
    // Auth API
    val authApi: AuthApiService by lazy {
        BaseApiService.createService()
    }
    
    // Phone API
    val phoneApi: PhoneApiService by lazy {
        BaseApiService.createService()
    }
    
    // Alert API
    val alertApi: AlertApiService by lazy {
        BaseApiService.createService()
    }
    
    // User API
    val userApi: UserApiService by lazy {
        BaseApiService.createService()
    }
    
    // Family API
    val familyApi: FamilyApiService by lazy {
        BaseApiService.createService()
    }
    
    // Report API
    val reportApi: ReportApiService by lazy {
        BaseApiService.createService()
    }
    
    // Screenshot API
    val screenshotApi: ScreenshotApiService by lazy {
        BaseApiService.createService()
    }
    
    // Text-to-Speech API
    val textToSpeechApi: TextToSpeechApiService by lazy {
        BaseApiService.createService()
    }

    val scamDetectionApi: ScamDetectionApiService by lazy {
        BaseApiService.createService()
    }
    
//    // Call API
//    val callApi: CallApiService by lazy {
//        BaseApiService.createService()
//    }
} 