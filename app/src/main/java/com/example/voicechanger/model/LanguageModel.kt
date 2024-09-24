package com.example.voicechanger.model

import com.example.voicechanger.R

data class LanguageModel (
    val flag: Int,
    val languageName: String,
    val originName: String,
    val locale: String,
    var isCheck: Boolean = false
) {
    companion object {
        val languages = listOf(
            LanguageModel(R.mipmap.ic_english, "English", "(English)", "en"),
            LanguageModel(R.mipmap.ic_portuguese, "Portuguese", "(Português)", "pt"),
            LanguageModel(R.mipmap.ic_france, "French", "(Français)", "fr"),
            LanguageModel(R.mipmap.ic_german, "German", "(Deutsch)", "de"),
            LanguageModel(R.mipmap.ic_hindi, "Hindi", "(हिंदी)", "hi"),
            LanguageModel(R.mipmap.ic_china, "Chinese", "(汉语)", "zh"),
            LanguageModel(R.mipmap.ic_spanish, "Spanish", "(Española)", "es")
        )
    }
}