package com.example.trustie.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayer @Inject constructor() {
    
    private var mediaPlayer: MediaPlayer? = null
    
    fun playAudioFile(filePath: String, onCompletion: () -> Unit = {}) {
        try {
            // Stop any currently playing audio
            stopAudio()
            
            val file = File(filePath)
            if (!file.exists()) {
                Log.e("AudioPlayer", "Audio file does not exist: $filePath")
                return
            }
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                setOnCompletionListener {
                    Log.d("AudioPlayer", "Audio playback completed")
                    onCompletion()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioPlayer", "MediaPlayer error: what=$what, extra=$extra")
                    onCompletion()
                    true
                }
                start()
            }
            
            Log.d("AudioPlayer", "Started playing audio: $filePath")
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error playing audio file", e)
            onCompletion()
        }
    }
    
    fun stopAudio() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.stop()
            }
            player.release()
        }
        mediaPlayer = null
        Log.d("AudioPlayer", "Stopped audio playback")
    }
    
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
    
    fun pauseAudio() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                Log.d("AudioPlayer", "Paused audio playback")
            }
        }
    }
    
    fun resumeAudio() {
        mediaPlayer?.let { player ->
            if (!player.isPlaying) {
                player.start()
                Log.d("AudioPlayer", "Resumed audio playback")
            }
        }
    }
} 