package com.example.voicechanger.data

import android.content.Context
import com.example.voicechanger.R
import com.example.voicechanger.model.TypeEffectModel

class TypeEffectDataSource(
    private val context: Context
) {
    fun getTypeEffectList(): List<TypeEffectModel> = listOf(
        TypeEffectModel(context.getString(R.string.all_effects), true),
        TypeEffectModel(context.getString(R.string.scary_effects), false),
        TypeEffectModel(context.getString(R.string.other_effects), false),
        TypeEffectModel(context.getString(R.string.people_effects), false),
        TypeEffectModel(context.getString(R.string.robot_effects), false)
    )
}