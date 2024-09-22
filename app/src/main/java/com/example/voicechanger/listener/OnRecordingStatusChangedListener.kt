package com.example.voicechanger.listener

interface OnRecordingStatusChangedListener {
    fun onStopRecording(path: String)
    fun onTimerChanged(i: Long)
}