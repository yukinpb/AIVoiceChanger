package com.example.voicechanger.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TypeEffectModel (
    var type: String?,
    var isActive: Boolean
) : Parcelable