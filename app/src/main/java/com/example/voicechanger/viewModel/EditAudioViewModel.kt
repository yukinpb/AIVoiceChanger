package com.example.voicechanger.viewModel

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.example.voicechanger.base.BaseViewModel
import com.example.voicechanger.model.AudioModel
import com.example.voicechanger.utils.getDuration
import com.example.voicechanger.utils.getSize
import com.example.voicechanger.utils.getVoiceEffectDirPath
import com.example.voicechanger.utils.getVoiceRecordDirPath
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import java.util.Timer
import java.util.TimerTask
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditAudioViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseViewModel() {
    private var mediaPlayer: MediaPlayer? = null
    private var timerTask: TimerTask? = null

    private var audioFilePath = ""
    private var tempOutputFilePath = ""
    private var finalFilePath = ""

    private val outputDir = context.getVoiceEffectDirPath()

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress

    private val _isPlaying = MutableLiveData(true)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _maxDuration = MutableLiveData(0)
    val maxDuration: LiveData<Int> = _maxDuration

    private var currentFileNamePlay = audioFilePath

    fun setRecordingPath(path: String) {
        audioFilePath = path
        currentFileNamePlay = audioFilePath
        tempOutputFilePath = audioFilePath.replace(Regex("\\.[^.]*$"), "") + "_temp.wav"
    }

    fun setFinalFileName(fileName: String) {
        finalFilePath = "${outputDir}/${fileName}.wav"
    }

    fun getCurrentFileNamePlay(): String {
        return currentFileNamePlay
    }

    fun playAudio() {
        try {
            mediaPlayer = MediaPlayer().apply {
                Log.d(TAG, "playAudio: $currentFileNamePlay")
                setDataSource(currentFileNamePlay)
                prepare()
                isLooping = true
                start()
            }
            startTimer()
            getMaxDuration()
            _isPlaying.postValue(true)
        } catch (e: IOException) {
            Log.e(TAG, "prepare() failed")
        }
    }

    private fun getMaxDuration()  {
        val duration = mediaPlayer?.duration
        Log.d(TAG, "getMaxDuration: $duration")
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
            mediaPlayer?.start()
            startTimer()
            _isPlaying.postValue(true)
        }
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

    fun saveAudio(showConfirmDialog: (fileName: String, onConfirm: () -> Unit) -> Unit): Boolean {
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
        val directory = File(context.getVoiceRecordDirPath())

        if (directory.exists()) {
            directory.listFiles()?.forEach { file ->
                if (file.name.contains("_temp")) {
                    file.delete()
                }
            }
        }
    }

    fun getAudioSaved(): AudioModel {
        val file = File(finalFilePath)
        val duration = file.getDuration()
        val size = file.getSize()
        return AudioModel(finalFilePath, file.name, duration, file.lastModified(), size)
    }

    fun trimAudio(startTime: String, endTime: String) {
        mediaPlayer?.stop()
        val outputFilePath = "${context.getVoiceRecordDirPath()}/${UUID.randomUUID()}_temp.wav"
        val command = arrayOf(
            "-i",
            currentFileNamePlay,
            "-ss",
            startTime,
            "-to",
            endTime,
            "-c",
            "copy",
            outputFilePath
        )
        executeFFmpegCommand(command, outputFilePath)
    }

    fun cutAudio(cutStartTime: String, cutEndTime: String) {
        mediaPlayer?.stop()
        val tempFilePath1 = "${context.getVoiceRecordDirPath()}/${System.currentTimeMillis()}_1_temp.wav"
        val tempFilePath2 = "${context.getVoiceRecordDirPath()}/${System.currentTimeMillis()}_2_temp.wav"
        val outputFilePath = "${context.getVoiceRecordDirPath()}/${System.currentTimeMillis()}_end_temp.wav"

        val command1 = arrayOf(
            "-i",
            currentFileNamePlay,
            "-ss",
            "0",
            "-to",
            cutStartTime,
            "-c",
            "copy",
            tempFilePath1
        )

        val command2 = arrayOf(
            "-i", currentFileNamePlay, "-ss", cutEndTime, "-c", "copy", tempFilePath2
        )
        executeFFmpegCommand(command1, tempFilePath1, false) {
            executeFFmpegCommand(command2, tempFilePath2, false) {
                val command = arrayOf(
                    "-i",
                    tempFilePath1,
                    "-i",
                    tempFilePath2,
                    "-filter_complex",
                    "[0:a][1:a]concat=n=2:v=0:a=1",
                    outputFilePath
                )
                executeFFmpegCommand(command, outputFilePath)
            }
        }
    }

    private fun executeFFmpegCommand(
        command: Array<String>,
        outputFilePath: String,
        isPlay: Boolean = true,
        onComplete: () -> Unit = {}
    ) {
        try {
            FFmpeg.execute(command)
            val rc = Config.getLastReturnCode()
            val output = Config.getLastCommandOutput()

            when (rc) {
                0 -> {
                    hideLoading()
                    if (isPlay) {
                        currentFileNamePlay = outputFilePath
                        playAudio()
                    }
                    Log.i(TAG, "Command execution completed successfully.")
                    onComplete()
                }
                1 -> {
                    hideLoading()
                    Log.e(TAG, "Failed to execute FFmpeg command: $output")
                }
                else -> {
                    hideLoading()
                    Log.e(TAG, "Failed to execute FFmpeg command: $output")
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to execute FFmpeg command: $e")
            e.printStackTrace()
        } catch (e: InterruptedException) {
            Log.e(TAG, "Failed to execute FFmpeg command: $e")
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "EditAudioViewModel"
    }
}