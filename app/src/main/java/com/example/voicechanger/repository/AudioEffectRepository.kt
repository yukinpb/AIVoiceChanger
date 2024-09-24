package com.example.voicechanger.repository

import com.example.voicechanger.data.AudioEffectDataSource
import com.example.voicechanger.model.AudioEffectModel
import javax.inject.Inject

interface AudioEffectRepository {
    fun getAllEffect() : List<AudioEffectModel>
    fun getRobotEffect() : List<AudioEffectModel>
    fun getPeopleEffect() : List<AudioEffectModel>
    fun getScaryEffect() : List<AudioEffectModel>
    fun getOtherEffect() : List<AudioEffectModel>
    fun getAIEffect() : List<AudioEffectModel>
}

class AudioEffectRepositoryImpl @Inject constructor(
    private val dataSource: AudioEffectDataSource
) : AudioEffectRepository {

    override fun getAllEffect(): List<AudioEffectModel> {
        return dataSource.getAllEffect()
    }

    override fun getRobotEffect(): List<AudioEffectModel> {
        return dataSource.getRobotEffect()
    }

    override fun getPeopleEffect(): List<AudioEffectModel> {
        return dataSource.getPeopleEffect()
    }

    override fun getScaryEffect(): List<AudioEffectModel> {
        return dataSource.getScaryEffect()
    }

    override fun getOtherEffect(): List<AudioEffectModel> {
        return dataSource.getOtherEffect()
    }

    override fun getAIEffect(): List<AudioEffectModel> {
        return dataSource.getAIEffect()
    }
}