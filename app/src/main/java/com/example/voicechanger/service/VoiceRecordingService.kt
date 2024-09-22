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
import androidx.core.app.NotificationCompat
import com.example.voicechanger.R
import com.example.voicechanger.listener.OnRecordingStatusChangedListener
import com.example.voicechanger.utils.getVoiceRecordDirPath
import java.io.File
import java.util.Timer
import java.util.TimerTask

class VoiceRecordingService: Service() {

    private var strFileName: String? = null
    private var strFilePath: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var timerTask: TimerTask? = null
    private var startTime = 0L
    private var elapsedTime = 0L
    private var onRecordingStatusChangedListener: OnRecordingStatusChangedListener? = null

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
        mediaRecorder?.let { stopRecording() }
        onRecordingStatusChangedListener = null
    }

    fun startRecording(maxDuration: Int) {
        try {
            startForeground(2, notificationCreate())
            setFileNamePath()
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(applicationContext)
            } else {
                MediaRecorder()
            }
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(strFilePath)
                setMaxDuration(maxDuration)
                setAudioChannels(1)
                setAudioSamplingRate(44100)
                setOnInfoListener { _, what, _ ->
                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                        stopRecording()
                    }
                }
                prepare()
                start()
            }
            startTimer()
        } catch (e: Exception) { }
    }

    private fun setFileNamePath() {
        strFileName = "Recording_${System.currentTimeMillis()}"
        strFilePath = "${this.getVoiceRecordDirPath()}/$strFileName.mp3"
    }

    private fun startTimer() {
        val timer = Timer()
        startTime = System.currentTimeMillis()
        timerTask = object : TimerTask() {
            override fun run() {
                timerTask?.let {
                    val currentTime = System.currentTimeMillis()
                    elapsedTime = currentTime - startTime
                    onRecordingStatusChangedListener?.onTimerChanged(elapsedTime)
                } ?: cancel()
            }
        }
        timer.schedule(timerTask, 1000, 1000)
    }

    fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            timerTask?.cancel()
            timerTask = null
            onRecordingStatusChangedListener?.onStopRecording(strFilePath ?: "")
            if (onRecordingStatusChangedListener == null) {
                stopSelf()
            }
            stopForeground(STOP_FOREGROUND_REMOVE)
        } catch (e: Exception) {
            println("RecordingService.stopRecording e = $e")
        }
    }

    fun resetRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            elapsedTime = 0L
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
            timerTask?.cancel()
            timerTask = null
        } catch (e: Exception) {
            println("RecordingService.pauseRecording e = $e")
        }
    }

    fun resumeRecording() {
        try {
            mediaRecorder?.resume()
            startTimer()
        } catch (e: Exception) {
            println("RecordingService.resumeRecording e = $e")
        }
    }

    private fun notificationCreate(): Notification {
        return NotificationCompat.Builder(
            applicationContext,
            if (Build.VERSION.SDK_INT >= 26) notificationChannelCreate() else ""
        )
            .setSmallIcon(R.mipmap.ic_mic_white_36dp)
            .setContentTitle(getString(R.string.notification_recording))
            .setOngoing(true)
            .build()
    }

    private fun notificationChannelCreate(): String {
        if (Build.VERSION.SDK_INT >= 26) {
            val notificationChannel = NotificationChannel(
                "recording_service",
                "Recording Service",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.lightColor = -16776961
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                notificationChannel
            )
        }
        return "recording_service"
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        stopRecording()
    }
}