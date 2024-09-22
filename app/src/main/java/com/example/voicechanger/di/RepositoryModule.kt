package com.example.voicechanger.di

import com.example.voicechanger.repository.AudioEffectRepository
import com.example.voicechanger.repository.AudioEffectRepositoryImpl
import com.example.voicechanger.repository.TypeEffectRepository
import com.example.voicechanger.repository.TypeEffectRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTypeEffectRepository(
        typeEffectRepositoryImpl: TypeEffectRepositoryImpl
    ): TypeEffectRepository

    @Binds
    abstract fun bindAudioEffectRepository(
        audioEffectRepositoryImpl: AudioEffectRepositoryImpl
    ): AudioEffectRepository
}