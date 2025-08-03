package com.example.trustie.di

import com.example.trustie.repository.authrepo.AuthRepository
import com.example.trustie.repository.authrepo.AuthRepositoryImpl
import com.example.trustie.repository.phonerepo.PhoneRepository
import com.example.trustie.repository.phonerepo.PhoneRepositoryImpl
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}

@Module
@InstallIn(SingletonComponent::class)
object AppModuleObject {
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
} 