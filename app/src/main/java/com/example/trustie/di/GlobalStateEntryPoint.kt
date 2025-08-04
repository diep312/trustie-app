package com.example.trustie.di;

import com.example.trustie.data.GlobalStateManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface GlobalStateEntryPoint {
    fun globalStateManager(): GlobalStateManager
}
