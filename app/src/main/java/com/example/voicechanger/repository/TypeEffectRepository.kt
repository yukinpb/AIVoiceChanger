package com.example.voicechanger.repository

import com.example.voicechanger.data.TypeEffectDataSource
import com.example.voicechanger.model.TypeEffectModel
import javax.inject.Inject

interface TypeEffectRepository {
    fun getTypeEffectList() : List<TypeEffectModel>
}

class TypeEffectRepositoryImpl @Inject constructor(
    private val dataSource: TypeEffectDataSource
) : TypeEffectRepository {

    override fun getTypeEffectList(): List<TypeEffectModel> {
        return dataSource.getTypeEffectList()
    }
}