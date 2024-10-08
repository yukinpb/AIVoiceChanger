package com.example.voicechanger.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.voicechanger.base.BaseViewModel
import com.example.voicechanger.model.AudioModel
import com.example.voicechanger.module.BASSMediaPlayer
import com.example.voicechanger.module.ChangeEffectModule
import com.example.voicechanger.module.MediaListener
import com.example.voicechanger.repository.TypeEffectRepository
import com.example.voicechanger.utils.getDuration
import com.example.voicechanger.utils.getSize
import com.example.voicechanger.utils.getVoiceEffectDirPath
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@HiltViewModel
class ChangeEffectViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioChanger: ChangeEffectModule,
    private val typeEffectRepository: TypeEffectRepository
) : BaseViewModel() {
    private var mediaPlayer: BASSMediaPlayer? = null
    private var timerTask: TimerTask? = null

    private var recordFilePath = ""

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress

    private val _isVolumeOn = MutableLiveData(true)
    val isVolumeOn: LiveData<Boolean> = _isVolumeOn

    private val _isPlaying = MutableLiveData(true)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _maxDuration = MutableLiveData(0)
    val maxDuration: LiveData<Int> = _maxDuration

    private var finalFilePath = ""

    fun setRecordingPath(path: String) {
        recordFilePath = path
    }

    fun getTypeEffectList() = typeEffectRepository.getTypeEffectList()

    fun playAudio(effectId: Int = 1) {
        audioChanger.setAudioPath(recordFilePath)
        audioChanger.prepare()
        mediaPlayer = audioChanger.getMediaPlayer()
        audioChanger.applyEffect(effectId)
        getMaxDuration()
        startTimer()
        audioChanger.setMediaListener(object : MediaListener {
            override fun onMediaErrorListener() {
                TODO("Not yet implemented")
            }

            override fun onMediaCompleteListener() {
                _isPlaying.postValue(false)
            }
        })
        _isPlaying.postValue(true)
    }

    private fun getMaxDuration() {
        val duration = mediaPlayer?.getDuration()
        _maxDuration.postValue(duration)
    }

    fun pauseAudio() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            _isPlaying.postValue(false)
        }
    }

    fun resumeAudio() {
        Log.d("hainv", "resumeAudio: 1")
        if (mediaPlayer?.isEnded == true) {
            Log.d("hainv", "resumeAudio: 2")
            mediaPlayer?.restart()
        } else {
            Log.d("hainv", "resumeAudio: 3")

            mediaPlayer?.start()
        }
        _isPlaying.postValue(true)
    }

    fun resetAudio() {
        mediaPlayer?.restart()
        _isPlaying.postValue(true)
    }

    fun stopAudio() {
        stopTimer()
        mediaPlayer?.release()
        mediaPlayer = null
        _progress.postValue(0)
        _isPlaying.postValue(false)
    }

    fun changeVolume() {
        val isVolumeOn = _isVolumeOn.value ?: true
        _isVolumeOn.postValue(!isVolumeOn)
        if (isVolumeOn) {
            mediaPlayer?.setVolume(0f)
        } else {
            mediaPlayer?.setVolume(1f)
        }
    }

    fun seekTo(position: Int) {
        stopTimer()
        mediaPlayer?.seekTo(position / 1000)
        _progress.postValue(position)
        startTimer()
    }

    private fun startTimer() {
        val timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                if (mediaPlayer?.isPlaying == true) {
                    val currentPosition = mediaPlayer?.getCurrentPosition() ?: 0
                    _progress.postValue(currentPosition)
                } else {
                    timer.cancel()
                }
            }
        }
        timer.schedule(timerTask, 0, 100)
    }

    private fun stopTimer() {
        timerTask?.cancel()
        timerTask = null
    }

    fun applyEffect(effectId: Int) {
        startTimer()
        _progress.postValue(0)
        _isPlaying.postValue(false)
        playAudio(effectId)
    }

    fun saveAudio(fileName: String, showConfirmDialog: (fileName: String, onConfirm: () -> Unit) -> Unit) : Boolean {
        var isSaved = false
        finalFilePath = context.getVoiceEffectDirPath() + "/" + "${fileName}.wav"
        val finalFile = File(finalFilePath)

        if (finalFile.exists()) {
            showConfirmDialog(finalFile.name) {
                finalFile.delete()
                showLoading()
                audioChanger.saveEffect(finalFile) {
                    hideLoading()
                    isSaved = true
                }
            }
        } else {
            showLoading()
            audioChanger.saveEffect(finalFile) {
                hideLoading()
                isSaved = true
            }
        }

        return isSaved
    }

    fun getAudioSaved(): AudioModel {
        val file = File(finalFilePath)
        val duration = file.getDuration()
        val size = file.getSize()
        return AudioModel(finalFilePath, file.name, duration, file.lastModified(), size)
    }
}