package com.example.voicechanger.viewModel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.voicechanger.base.BaseViewModel
import com.example.voicechanger.listener.OnRecordingStatusChangedListener
import com.example.voicechanger.service.VoiceRecordingService
import com.example.voicechanger.utils.milliSecFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    var recordingVoice: VoiceRecordingService? = null

    var recordingPath: String = ""

    private val _elapsedTime = MutableLiveData<String>()
    val elapsedTime: LiveData<String> = _elapsedTime

    private val onRecordingStatusChangedListener = object : OnRecordingStatusChangedListener {
        override fun onStopRecording(path: String) {
            recordingPath = path
        }

        override fun onTimerChanged(i: Long) {
            _elapsedTime.postValue(i.milliSecFormat())
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            val binder = iBinder as VoiceRecordingService.LocalBinder
            recordingVoice = binder.getService()
            recordingVoice?.setOnRecordingStatusChangedListener(onRecordingStatusChangedListener)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            recordingVoice?.setOnRecordingStatusChangedListener(null)
        }
    }

    fun connectService(intent: Intent) {
        context.startService(intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun startRecording() {
        recordingVoice?.startRecording(0)
    }

    fun pauseRecording() {
        recordingVoice?.pauseRecording()
    }

    fun resumeRecording() {
        recordingVoice?.resumeRecording()
    }

    fun resetRecording() {
        recordingVoice?.resetRecording()
    }

    fun stopRecording() {
        recordingVoice?.stopRecording()
    }
}