package com.example.voicechanger.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecordingModel(
    var name: String?,
    var path: String?,
    var length: Long,
    var timeAdded: Long,
    var fav: Int
) : Parcelable