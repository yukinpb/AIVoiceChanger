package com.example.voicechanger.viewModel

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.voicechanger.base.BaseViewModel
import com.example.voicechanger.model.AudioModel
import com.example.voicechanger.module.AudioChangerModule
import com.example.voicechanger.repository.TypeEffectRepository
import com.example.voicechanger.utils.getDuration
import com.example.voicechanger.utils.getSize
import com.example.voicechanger.utils.getVoiceEffectDirPath
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@HiltViewModel
class ChangeEffectViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioChanger: AudioChangerModule,
    private val typeEffectRepository: TypeEffectRepository
) : BaseViewModel() {
    private var mediaPlayer: MediaPlayer? = null
    private var timerTask: TimerTask? = null

    private var recordFilePath = ""
    private var tempOutputFilePath = ""
    private var finalFilePath = ""

    private val outputDir = context.getVoiceEffectDirPath()

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress

    private val _isVolumeOn = MutableLiveData(true)
    val isVolumeOn: LiveData<Boolean> = _isVolumeOn

    private val _isPlaying = MutableLiveData(true)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _maxDuration = MutableLiveData(0)
    val maxDuration: LiveData<Int> = _maxDuration

    private val _playbackSpeed = MutableLiveData(1.0f)
    val playbackSpeed: LiveData<Float> = _playbackSpeed

    private val playbackSpeeds = listOf(0.5f, 1.0f, 1.5f, 2.0f)

    private var hasPlayerEnd = false

    private var currentFileNamePlay = recordFilePath

    fun setRecordingPath(path: String) {
        recordFilePath = path
        currentFileNamePlay = recordFilePath
        tempOutputFilePath = recordFilePath.replace(Regex("\\.[^.]*$"), "") + "_temp.wav"
    }

    fun setFinalFileName(fileName: String) {
        finalFilePath = "${outputDir}/${fileName}.wav"
    }

    fun getTypeEffectList() = typeEffectRepository.getTypeEffectList()

    fun playAudio() {
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(currentFileNamePlay)
                prepare()
                start()
                setOnCompletionListener {
                    _isPlaying.postValue(false)
                    hasPlayerEnd = true
                }
            }
            startTimer()
            getMaxDuration()
            _isPlaying.postValue(true)
        } catch (e: IOException) {
            Log.e(TAG, "prepare() failed")
        }
    }

    private fun getMaxDuration() {
        val duration = mediaPlayer?.duration
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
            stop()
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
            mediaPlayer?.setVolume(0f, 0f)
        } else {
            mediaPlayer?.setVolume(1f, 1f)
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        _progress.postValue(position / 1000)
    }

    private fun setPlaybackSpeed(speed: Float) {
        mediaPlayer?.let {
            it.playbackParams = it.playbackParams.setSpeed(speed)
            _playbackSpeed.value = speed
        }
    }

    fun changeSpeed() {
        val currentSpeed = _playbackSpeed.value ?: 1.0f
        val nextSpeed =
            playbackSpeeds[(playbackSpeeds.indexOf(currentSpeed) + 1) % playbackSpeeds.size]
        setPlaybackSpeed(nextSpeed)
    }

    private fun startTimer() {
        val timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                if (mediaPlayer?.isPlaying == true) {
                    val currentPosition = mediaPlayer?.currentPosition ?: 0
                    _progress.postValue(currentPosition)
                } else {
                    timer.cancel()
                }
            }
        }
        timer.schedule(timerTask, 0, 100)
    }

    fun applyEffect(effectId: Int) {
        mediaPlayer?.stop()
        if (audioChanger.applyEffect(
                effectId, recordFilePath, tempOutputFilePath,
                onPrepare = {
                    showLoading()
                },
                onSuccess = {
                    Log.d(TAG, "Effect applied successfully.")
                    currentFileNamePlay = tempOutputFilePath
                    hideLoading()
                    playAudio()
                },
                onCancel = {
                    hideLoading()
                    currentFileNamePlay = recordFilePath
                    playAudio()
                    Log.i(TAG, "Effect application cancelled.")
                },
                onError = {
                    hideLoading()
                    currentFileNamePlay = recordFilePath
                    playAudio()
                    Log.e(TAG, "Failed to apply effect: $it")
                },
            )
        ) {
            currentFileNamePlay = tempOutputFilePath
        } else {
            currentFileNamePlay = recordFilePath
            playAudio()
        }
    }

    fun saveAudio(showConfirmDialog: (fileName: String, onConfirm: () -> Unit) -> Unit) : Boolean {
        val tempFile = File(currentFileNamePlay)
        val finalFile = File(finalFilePath)

        if (tempFile.exists()) {
            if (finalFile.exists()) {
                showConfirmDialog(finalFile.name) {
                    finalFile.delete()
                    try {
                        tempFile.copyTo(finalFile, overwrite = true)
                        Log.i(TAG, "Processed audio saved to permanent storage.")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to save audio: ${e.message}")
                    }
                }
                return false
            } else {
                try {
                    tempFile.copyTo(finalFile, overwrite = true)
                    Log.i(TAG, "Processed audio saved to permanent storage.")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to save audio: ${e.message}")
                }
                return true
            }
        } else {
            Log.e(TAG, "Temporary file does not exist.")
            return false
        }
    }

    fun deleteAllTempFiles() {
        val tempFile = File(tempOutputFilePath)
        if (tempFile.exists()) {
            tempFile.delete()
        }
    }

    fun getAudioSaved(): AudioModel {
        val file = File(finalFilePath)
        val duration = file.getDuration()
        val size = file.getSize()
        return AudioModel(finalFilePath, file.name, duration, file.lastModified(), size)
    }

    companion object {
        const val TAG = "hainv"
    }
}