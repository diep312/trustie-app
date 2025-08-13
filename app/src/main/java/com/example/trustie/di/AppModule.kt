package com.example.trustie.di

import com.example.trustie.repository.authrepo.AuthRepository
import com.example.trustie.repository.authrepo.AuthRepositoryImpl
import com.example.trustie.repository.phonerepo.PhoneRepository
import com.example.trustie.repository.phonerepo.PhoneRepositoryImpl
import com.example.trustie.repository.imagerepo.ImageVerificationRepository
import com.example.trustie.repository.imagerepo.ImageVerificationRepositoryImpl
import com.example.trustie.repository.ttsrepo.TextToSpeechRepository
import com.example.trustie.repository.ttsrepo.TextToSpeechRepositoryImpl
import com.example.trustie.repository.connectrepo.ConnectionRepository
import com.example.trustie.repository.callrepo.CallHistoryRepository
import com.example.trustie.repository.reportrepo.ReportRepository
import android.content.Context
import com.example.trustie.repository.audiotranscriptrepo.AudioTranscriptRepository
import com.example.trustie.repository.audiotranscriptrepo.AudioTranscriptRepositoryImpl
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
    
    @Binds
    @Singleton
    abstract fun bindPhoneRepository(
        phoneRepositoryImpl: PhoneRepositoryImpl
    ): PhoneRepository
    
    @Binds
    @Singleton
    abstract fun bindImageVerificationRepository(
        imageVerificationRepositoryImpl: ImageVerificationRepositoryImpl
    ): ImageVerificationRepository
    
    @Binds
    @Singleton
    abstract fun bindTextToSpeechRepository(
        textToSpeechRepositoryImpl: TextToSpeechRepositoryImpl
    ): TextToSpeechRepository

    @Binds
    @Singleton
    abstract fun bindAudioTranscriptRepository(
        audioTranscriptRepositoryimpl: AudioTranscriptRepositoryImpl
    ): AudioTranscriptRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModuleObject {
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
    
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }
    
    @Provides
    @Singleton
    fun provideConnectionRepository(): ConnectionRepository {
        return ConnectionRepository()
    }
    
    @Provides
    @Singleton
    fun provideCallHistoryRepository(): CallHistoryRepository {
        return CallHistoryRepository()
    }
    
    @Provides
    @Singleton
    fun provideReportRepository(): ReportRepository {
        return ReportRepository()
    }
} 