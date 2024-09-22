package com.example.voicechanger.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AudioEffectModel(
    var id: Int,
    var name: String,
    var nameOrigin: String?,
    var iconSelected: Int,
    var iconUnSelected: Int,
    var thumb: Int,
    var isActive: Boolean
) : Parcelable