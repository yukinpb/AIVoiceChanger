package com.example.voicechanger.model

data class AudioAttrModel(
    val id: Int,
    val icon: String,
    val name: String,
    val asetrate: String = "",
    val atempo: String = "",
    val aecho: String = "",
    val volume: String? = null,
    val areverse: Boolean = false,
)