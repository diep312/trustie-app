//package com.example.trustie.di
//
//import android.content.Context
//import com.example.trustie.data.api.AuthApiService
//import com.example.trustie.data.remote.ImageVerificationApiService
//import com.example.trustie.data.repository.AuthRepositoryImpl
//import com.example.trustie.data.repository.ImageVerificationRepositoryImpl
//import com.example.trustie.domain.repository.AuthRepository
//import com.example.trustie.domain.repository.ImageVerificationRepository
//import dagger.Binds
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//abstract class AppModule { // Vẫn là abstract class vì có @Binds
//
//    // Các phương thức @Provides phải nằm trong companion object và được đánh dấu @JvmStatic
//    companion object {
//        @Provides
//        @Singleton
//        @JvmStatic // Bắt buộc phải là static khi module là abstract class
//        fun provideOkHttpClient(): OkHttpClient {
//            return OkHttpClient.Builder()
//                .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
//                .build()
//        }
//
//        @Provides
//        @Singleton
//        @JvmStatic // Bắt buộc phải là static
//        fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
//            return Retrofit.Builder()
//                .baseUrl("http://54.90.139.125")
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(okHttpClient)
//                .build()
//        }
//
//        @Provides
//        @Singleton
//        @JvmStatic // Bắt buộc phải là static
//        fun provideImageVerificationApiService(retrofit: Retrofit): ImageVerificationApiService {
//            return retrofit.create(ImageVerificationApiService::class.java)
//        }
//
//        @Provides
//        @Singleton
//        @JvmStatic // Bắt buộc phải là static
//        fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
//            return retrofit.create(AuthApiService::class.java)
//        }
//
//        @Provides
//        @Singleton
//        @JvmStatic // Bắt buộc phải là static
//        fun provideImageVerificationRepository(
//            apiService: ImageVerificationApiService,
//            @ApplicationContext context: Context
//        ): ImageVerificationRepository {
//            return ImageVerificationRepositoryImpl(apiService, context)
//        }
//    }
//
//    // Phương thức @Binds vẫn nằm ngoài companion object và là abstract
//    @Binds
//    @Singleton
//    abstract fun bindAuthRepository(
//        authRepositoryImpl: AuthRepositoryImpl
//    ): AuthRepository
//}


package com.example.trustie.di

import android.content.Context
import com.example.trustie.data.api.AuthApiService
import com.example.trustie.data.remote.ImageVerificationApiService
import com.example.trustie.data.repository.AuthRepositoryImpl
import com.example.trustie.data.repository.ImageVerificationRepositoryImpl
import com.example.trustie.domain.repository.AuthRepository
import com.example.trustie.domain.repository.ImageVerificationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule { // Vẫn là abstract class vì có @Binds

    // Các phương thức @Provides phải nằm trong companion object và được đánh dấu @JvmStatic
    companion object {
        @Provides
        @Singleton
        @JvmStatic // Bắt buộc phải là static khi module là abstract class
        fun provideOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                .build()
        }

        @Provides
        @Singleton
        @JvmStatic // Bắt buộc phải là static
        fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl("http://54.90.139.125") // <-- Đảm bảo đây là BASE URL CHÍNH XÁC của backend của bạn
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }

        @Provides
        @Singleton
        @JvmStatic // Bắt buộc phải là static
        fun provideImageVerificationApiService(retrofit: Retrofit): ImageVerificationApiService {
            return retrofit.create(ImageVerificationApiService::class.java)
        }

        @Provides
        @Singleton
        @JvmStatic // Bắt buộc phải là static
        fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
            return retrofit.create(AuthApiService::class.java)
        }

        @Provides
        @Singleton
        @JvmStatic // Bắt buộc phải là static
        fun provideImageVerificationRepository(
            apiService: ImageVerificationApiService,
            @ApplicationContext context: Context
        ): ImageVerificationRepository {
            return ImageVerificationRepositoryImpl(apiService, context)
        }

        // THÊM PHƯƠNG THỨC NÀY ĐỂ CUNG CẤP AuthRepositoryImpl
        @Provides
        @Singleton
        @JvmStatic
        fun provideAuthRepositoryImpl(
            @ApplicationContext context: Context, // Hilt sẽ cung cấp ApplicationContext
            apiService: AuthApiService
        ): AuthRepositoryImpl {
            return AuthRepositoryImpl(context, apiService)
        }
    }

    // Phương thức @Binds vẫn nằm ngoài companion object và là abstract
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}
