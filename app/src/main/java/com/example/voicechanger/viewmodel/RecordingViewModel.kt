package com.example.voicechanger.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.voicechanger.listener.OnRecordingStatusChangedListener
import com.example.voicechanger.model.RecordingModel
import com.example.voicechanger.service.VoiceRecordingService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    var recordingVoice: VoiceRecordingService? = null

    private val _amplitudeLiveData = MutableLiveData<Int>()
    val amplitudeLiveData: LiveData<Int> get() = _amplitudeLiveData

    private val _recordingModelLiveEvents = MutableLiveData<RecordingModel>()
    val recordingModelLiveEvents: LiveData<RecordingModel> get() = _recordingModelLiveEvents

    private val _isRecording = MutableLiveData<Boolean>(false)
    val isRecording: LiveData<Boolean> get() = _isRecording

    private val _isResumeRecording = MutableLiveData<Boolean>(false)
    val isResumeRecording: LiveData<Boolean> get() = _isResumeRecording

    private val _elapsedTime = MutableLiveData<Int>(0)
    val elapsedTime: LiveData<Int> get() = _elapsedTime

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            val binder = iBinder as VoiceRecordingService.LocalBinder
            recordingVoice = binder.getService()
            recordingVoice?.setOnRecordingStatusChangedListener(onRecordingStatusChangedListener)
            _isRecording.value = recordingVoice?.isRecording ?: false
            _isResumeRecording.value = recordingVoice?.isResumeRecording ?: false
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            recordingVoice?.setOnRecordingStatusChangedListener(null)
        }
    }

    private val onRecordingStatusChangedListener = object : OnRecordingStatusChangedListener {
        override fun onAmplitudeInfo(i: Int) {
            _amplitudeLiveData.postValue(i)
        }

        override fun onPauseRecording() {
            _isResumeRecording.postValue(false)
        }

        override fun onResumeRecording() {
            _isResumeRecording.postValue(true)
        }

        override fun onSkipRecording() {
            _isRecording.postValue(false)
            _isResumeRecording.postValue(false)
            _elapsedTime.postValue(0)
        }

        override fun onStartedRecording() {
            _isRecording.postValue(true)
            _isResumeRecording.postValue(true)
        }

        override fun onStopRecording(recordingModel: RecordingModel) {
            _isRecording.postValue(false)
            _isResumeRecording.postValue(false)
            _elapsedTime.postValue(0)
            _recordingModelLiveEvents.postValue(recordingModel)
        }

        override fun onTimerChanged(i: Int) {
            _elapsedTime.postValue(i)
        }
    }

    fun connectService(intent: Intent) {
        context.startService(intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun disconnectService(intent: Intent) {
        if (_isRecording.value == true) {
            context.unbindService(connection)
            if (_isRecording.value == false) {
                context.stopService(intent)
            }
            recordingVoice?.setOnRecordingStatusChangedListener(null)
        }
    }

    fun startRecording() {
        recordingVoice?.startRecording(0)
        _isRecording.postValue(true)
        _isResumeRecording.postValue(true)
    }

    fun stopRecording() {
        recordingVoice?.recordingStop()
    }

    fun pauseRecording() {
        recordingVoice?.pauseRecording()
    }

    fun resumeRecording() {
        recordingVoice?.resumeRecording()
    }

    fun skipRecording() {
        recordingVoice?.fileRecordSkip()
    }
}