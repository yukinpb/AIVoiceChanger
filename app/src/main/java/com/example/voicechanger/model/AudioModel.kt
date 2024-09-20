package com.example.voicechanger.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AudioModel(
    var path: String,
    var fileName: String,
    var duration: String,
    var dateCreate: Long,
    var size: String,
    var type: String
) : Parcelable