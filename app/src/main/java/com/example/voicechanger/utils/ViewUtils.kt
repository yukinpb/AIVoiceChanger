package com.example.voicechanger.utils

import android.os.SystemClock
import android.view.View
import com.example.voicechanger.utils.Constants.DURATION_TIME_CLICKABLE

private var lastClick: Long = 0

fun View.setOnSafeClickListener(
    duration: Long = DURATION_TIME_CLICKABLE,
    onClick: () -> Unit
) = setOnClickListener {
    if (SystemClock.elapsedRealtime() - lastClick >= duration) {
        onClick()
        lastClick = SystemClock.elapsedRealtime()
    }
}