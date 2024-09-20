package com.example.voicechanger.utils

import android.app.Activity
import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.voicechanger.R
import java.io.File

object FileUtils {
    fun getMainDirPath(context: Context): String {
        return try {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + context.resources.getString(R.string.app_name), "VoiceEffectAudio")
            Log.e("xz---", "getMainDirPath: voiceEffectAudioFilePath ::  $file")
            if (!file.exists()) {
                file.mkdirs()
            }
            file.absolutePath
        } catch (e: Exception) {
            context.filesDir.absolutePath
        }
    }

    fun getDirectory(activity: Activity): File {
        val file = File(Environment.getExternalStorageDirectory().path + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + activity.resources.getString(R.string.app_name) + "/VoiceEffects")
        Log.e("xz---", "getMainDirPath: voiceEffectDirPath ::  $file")
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    fun milliSecFormat(j: Long): String {
        val hours = (j / 3600000).toInt()
        val remainingMillis = j % 3600000
        val minutes = (remainingMillis / 60000).toInt()
        val seconds = Math.round((remainingMillis % 60000 / 1000).toFloat())

        val hoursStr = if (hours > 0) "$hours:" else ""
        val minutesStr = if (minutes < 10) "0$minutes" else "$minutes"
        val secondsStr = if (seconds < 10) "0$seconds" else "$seconds"

        return "$hoursStr$minutesStr:$secondsStr"
    }
}