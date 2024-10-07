package com.example.voicechanger.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.voicechanger.base.BaseViewModel
import com.example.voicechanger.model.AudioModel
import com.example.voicechanger.module.BASSMediaPlayer
import com.example.voicechanger.module.ChangeEffectModule
import com.example.voicechanger.repository.TypeEffectRepository
import com.example.voicechanger.utils.getDuration
import com.example.voicechanger.utils.getSize
import com.example.voicechanger.utils.getVoiceEffectDirPath
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
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

    private var hasPlayerEnd = false

    private var finalFilePath = ""

    fun init() {
        audioChanger.setAudioPath(recordFilePath)
        audioChanger.createMediaPlayer {
            _isPlaying.postValue(false)
            hasPlayerEnd = true
        }
        audioChanger.insertEffect(getVoiceEffect())
        mediaPlayer = audioChanger.getMediaPlayer()
        applyEffect(0)
    }

    fun setRecordingPath(path: String) {
        recordFilePath = path
    }

    private fun getVoiceEffect(): String? {
        return try {
            context.assets.open("effects.json").use { inputStream ->
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                String(buffer, StandardCharsets.UTF_8)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getTypeEffectList() = typeEffectRepository.getTypeEffectList()

    fun playAudio() {
        mediaPlayer?.start()
        startTimer()
        getMaxDuration()
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
        if (mediaPlayer?.isPlaying == false) {
            if (hasPlayerEnd) {
                mediaPlayer?.seekTo(0)
                hasPlayerEnd = false
            }
            mediaPlayer?.start()
            startTimer()
            _isPlaying.postValue(true)
        }
    }

    fun resetAudio() {
        mediaPlayer?.seekTo(0)
        mediaPlayer?.start()
        _isPlaying.postValue(true)
    }

    fun stopAudio() {
        timerTask?.cancel()
        timerTask = null
        mediaPlayer?.apply {
            release()
        }
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
        mediaPlayer?.seekTo(position)
        _progress.postValue(position / 1000)
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

    fun applyEffect(effectId: Int) {
        audioChanger.applyEffect(effectId)
    }

    fun saveAudio(fileName: String, showConfirmDialog: (fileName: String, onConfirm: () -> Unit) -> Unit) : Boolean {
        var isSaved = false
        finalFilePath = context.getVoiceEffectDirPath() + "/" + fileName
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