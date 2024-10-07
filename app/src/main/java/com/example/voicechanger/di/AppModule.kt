package com.example.voicechanger.di

import android.content.Context
import com.example.voicechanger.data.AudioEffectDataSource
import com.example.voicechanger.data.TypeEffectDataSource
import com.example.voicechanger.module.BASSMediaPlayer
import com.example.voicechanger.module.ChangeEffectModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAudioChanger(@ApplicationContext context: Context): ChangeEffectModule {
        return ChangeEffectModule(context)
    }

    @Provides
    @Singleton
    fun provideAudioEffectDataSource(@ApplicationContext context: Context): AudioEffectDataSource {
        return AudioEffectDataSource(context)
    }

    @Provides
    @Singleton
    fun provideTypeEffectDataSource(@ApplicationContext context: Context): TypeEffectDataSource {
        return TypeEffectDataSource(context)
    }
}