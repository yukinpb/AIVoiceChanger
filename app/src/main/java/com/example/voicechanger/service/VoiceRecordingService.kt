package com.example.voicechanger.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.DtsUtil
import com.example.voicechanger.listener.OnRecordingStatusChangedListener
import com.example.voicechanger.model.RecordingModel
import com.example.voicechanger.R
import com.example.voicechanger.utils.FileUtils
import java.io.File
import java.util.Timer
import java.util.TimerTask

class VoiceRecordingService(
    private val context: Context
) : Service() {

    var isRecording = false
    var isResumeRecording = false
    private var strFileName: String? = null
    private var strFilePath: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var timerTask: TimerTask? = null
    private var mElapsedMillis: Long = 0
    private  var onRecordingStatusChangedListener: OnRecordingStatusChangedListener? = null

    private val myBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): VoiceRecordingService = this@VoiceRecordingService
    }

    override fun onBind(intent: Intent?): IBinder = myBinder

    fun setOnRecordingStatusChangedListener(listener: OnRecordingStatusChangedListener?) {
        onRecordingStatusChangedListener = listener
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaRecorder?.let { recordingStop() }
        onRecordingStatusChangedListener = null
    }

    @OptIn(UnstableApi::class)
    fun startRecording(maxDuration: Int) {
        try {
            startForeground(2, notificationCreate())
            setFileNamePath()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mediaRecorder = MediaRecorder(context).apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(strFilePath)
                    setMaxDuration(maxDuration)
                    setAudioChannels(1)
                    setAudioSamplingRate(44100)
                    setAudioEncodingBitRate(DtsUtil.DTS_MAX_RATE_BYTES_PER_SECOND)
                    setOnInfoListener { _, what, _ ->
                        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                            recordingStop()
                        }
                    }
                    prepare()
                    start()
                }
            } else {
                mediaRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(strFilePath)
                    setMaxDuration(maxDuration)
                    setAudioChannels(1)
                    setAudioSamplingRate(44100)
                    setAudioEncodingBitRate(DtsUtil.DTS_MAX_RATE_BYTES_PER_SECOND)
                    setOnInfoListener { _, what, _ ->
                        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                            recordingStop()
                        }
                    }
                    prepare()
                    start()
                }
            }
            isRecording = true
            isResumeRecording = true
            timerStart()
        } catch (e: Exception) {
            Log.e(TAG, "startRecording(): prepare() failed $e")
        }
        onRecordingStatusChangedListener?.onStartedRecording()
    }

    private fun setFileNamePath() {
        strFileName = "Voice_effect_${System.currentTimeMillis()}"
        strFilePath = "${FileUtils.getMainDirPath(this)}/$strFileName.mp3"
    }

    private fun timerStart() {
        val timer = Timer()
        mElapsedMillis = 0
        timerTask = object : TimerTask() {
            override fun run() {
                timerTask?.let {
                    mElapsedMillis += 100
                    onRecordingStatusChangedListener?.onTimerChanged((mElapsedMillis / 1000).toInt())
                    mediaRecorder?.let { recorder ->
                        try {
                            onRecordingStatusChangedListener?.onAmplitudeInfo(recorder.maxAmplitude)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } ?: cancel()
            }
        }
        timer.schedule(timerTask, 100, 100)
    }

    fun recordingStop() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            isRecording = false
            isResumeRecording = false
            mediaRecorder = null
            timerTask?.cancel()
            timerTask = null
            val recordingModel = RecordingModel(strFileName, strFilePath, mElapsedMillis, System.currentTimeMillis(), 0)
            onRecordingStatusChangedListener?.onStopRecording(recordingModel)
            if (onRecordingStatusChangedListener == null) {
                stopSelf()
            }
            stopForeground(STOP_FOREGROUND_REMOVE)
        } catch (e: Exception) {
            println("RecordingService.stopRecording e = $e")
        }
    }

    fun fileRecordSkip() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            isRecording = false
            isResumeRecording = false
            mediaRecorder = null
            mElapsedMillis = 0
            onRecordingStatusChangedListener?.onSkipRecording()
            timerTask?.cancel()
            timerTask = null
            strFilePath?.let {
                val file = File(it)
                if (file.exists()) {
                    file.delete()
                }
            }
            if (onRecordingStatusChangedListener == null) {
                stopSelf()
            }
            stopForeground(STOP_FOREGROUND_REMOVE)
        } catch (e: Exception) {
            println("RecordingService.skipFileRecord e = $e")
        }
    }

    fun pauseRecording() {
        try {
            mediaRecorder?.pause()
            isResumeRecording = false
            onRecordingStatusChangedListener?.onPauseRecording()
            timerTask?.cancel()
            timerTask = null
        } catch (e: Exception) {
            println("RecordingService.pauseRecording e = $e")
        }
    }

    fun resumeRecording() {
        try {
            mediaRecorder?.resume()
            isResumeRecording = true
            onRecordingStatusChangedListener?.onResumeRecording()
            val timer = Timer()
            timerTask = object : TimerTask() {
                override fun run() {
                    timerTask?.let {
                        mElapsedMillis += 100
                        onRecordingStatusChangedListener?.onTimerChanged((mElapsedMillis / 1000).toInt())
                        mediaRecorder?.let { recorder ->
                            try {
                                onRecordingStatusChangedListener?.onAmplitudeInfo(recorder.maxAmplitude)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } ?: cancel()
                }
            }
            timer.schedule(timerTask, 100, 100)
        } catch (e: Exception) {
            println("RecordingService.resumeRecording e = $e")
        }
    }

    private fun notificationCreate(): Notification {
        return NotificationCompat.Builder(applicationContext, if (Build.VERSION.SDK_INT >= 26) notificationChannelCreate() else "")
            .setSmallIcon(R.drawable.ic_mic_white_36dp)
            .setContentTitle(getString(R.string.notification_recording))
            .setOngoing(true)
            .build()
    }

    private fun notificationChannelCreate(): String {
        if (Build.VERSION.SDK_INT >= 26) {
            val notificationChannel = NotificationChannel("recording_service", "Recording Service", NotificationManager.IMPORTANCE_LOW)
            notificationChannel.lightColor = -16776961
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(notificationChannel)
        }
        return "recording_service"
    }

    fun isRecording(): Boolean = isRecording

    fun isResumeRecording(): Boolean = isResumeRecording

    companion object {
        private const val TAG = "VoiceRecordingService"
    }
}