package com.example.voicechanger.model

data class LanguageModel (
    val flag: Int,
    val languageName: String,
    val originName: String,
    val locale: String,
    var isCheck: Boolean = false
)