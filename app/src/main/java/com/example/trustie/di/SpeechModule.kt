package com.example.trustie.di

import android.content.Context
import com.example.trustie.data.local.lm.ArpaLanguageModel
import com.example.trustie.data.local.wave2vec.OnnxWav2Vec2Manager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.json.JSONObject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpeechModule {

    @Provides
    @Singleton
    fun provideLanguageModel(@ApplicationContext context: Context): ArpaLanguageModel {
        return ArpaLanguageModel(context)
    }

    @Provides
    @Singleton
    fun provideVocab(@ApplicationContext context: Context): List<String> {
        val json = context.assets.open("models/vocab.json")
            .bufferedReader().use { it.readText() }
        val map = JSONObject(json)

        // We build the vocab list in index order [0..n]
        return List(map.length()) { idx ->
            map.keys().asSequence().first { key -> map.getInt(key) == idx }
        }
    }

    @Provides
    @Singleton
    fun provideOnnxModelManager(
        @ApplicationContext context: Context,
        vocab: List<String>
    ): OnnxWav2Vec2Manager {
        return OnnxWav2Vec2Manager(
            context = context,
            vocab = vocab,
            modelAssetPath = "models/wav2vec2_vi250h.onnx"
        ).apply {
            loadModel()
        }
    }
}