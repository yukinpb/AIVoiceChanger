package com.example.voicechanger.listener

import com.example.voicechanger.model.RecordingModel

interface OnRecordingStatusChangedListener {
    fun onAmplitudeInfo(i: Int)
    fun onPauseRecording()
    fun onResumeRecording()
    fun onSkipRecording()
    fun onStartedRecording()
    fun onStopRecording(recordingModel: RecordingModel)
    fun onTimerChanged(i: Int)
}