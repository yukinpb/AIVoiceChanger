package com.example.voicechanger.utils

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.widget.Toast

private var lastClick: Long = 0

fun View.setOnSafeClickListener(
    duration: Long = Constants.Timing.DURATION_TIME_CLICKABLE,
    onClick: () -> Unit
) = setOnClickListener {
    if (SystemClock.elapsedRealtime() - lastClick >= duration) {
        onClick()
        lastClick = SystemClock.elapsedRealtime()
    }
}