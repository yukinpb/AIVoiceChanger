package com.example.voicechanger.utils

fun Long.milliSecFormat(): String {
    val hours = (this / 3600000).toInt()
    val remainingMillis = this % 3600000
    val minutes = (remainingMillis / 60000).toInt()
    val seconds = Math.round((remainingMillis % 60000 / 1000).toFloat())

    val hoursStr = if (hours > 0) "$hours:" else ""
    val minutesStr = if (minutes < 10) "0$minutes" else "$minutes"
    val secondsStr = if (seconds < 10) "0$seconds" else "$seconds"

    return "$hoursStr$minutesStr:$secondsStr"
}

fun String.isValidName(): Boolean {
    val regex = "^[a-zA-Z0-9_]*$".toRegex()
    return this.isNotEmpty() && this.matches(regex)
}