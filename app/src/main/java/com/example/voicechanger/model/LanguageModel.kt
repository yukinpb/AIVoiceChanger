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
            LanguageModel(R.mipmap.ic_vietnam, "Vietnam", "(Việt Nam)", "vi")
        )
    }
}