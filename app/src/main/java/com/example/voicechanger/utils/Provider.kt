package com.example.voicechanger.utils

import com.example.voicechanger.R
import com.example.voicechanger.model.LanguageModel
import java.util.Locale

object LanguageProvider {
    fun getLanguages(): List<LanguageModel> {
        return listOf(
            LanguageModel(R.mipmap.ic_english, "English", Locale.US),
            LanguageModel(R.mipmap.ic_portuguese, "Portuguese", Locale("pt", "BR")),
            LanguageModel(R.mipmap.ic_france, "French", Locale.FRANCE),
            LanguageModel(R.mipmap.ic_german, "German", Locale.GERMANY),
            LanguageModel(R.mipmap.ic_hindi, "Hindi", Locale("hi", "IN")),
            LanguageModel(R.mipmap.ic_indonesia, "Indonesian", Locale("in", "ID")),
            LanguageModel(R.mipmap.ic_spanish, "Spanish", Locale("es", "ES"))
        )
    }
}