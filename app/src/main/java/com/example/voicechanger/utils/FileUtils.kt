package com.example.voicechanger.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Environment
import com.example.voicechanger.R
import java.io.File

fun Context.getVoiceRecordDirPath(): String {
    return try {
        val file = File(
            Environment.getExternalStorageDirectory().path + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + this.resources.getString(
                R.string.app_name
            ) + "/" + Constants.Directories.VOICE_RECORDER_DIR
        )
        if (!file.exists()) {
            file.mkdirs()
        }
        file.absolutePath
    } catch (e: Exception) {
        this.filesDir.absolutePath
    }
}

fun Context.getVoiceEffectDirPath(): String {
    return try {
        val file = File(
            Environment.getExternalStorageDirectory().path + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + this.resources.getString(
                R.string.app_name
            ) + "/" + Constants.Directories.VOICE_CHANGER_DIR
        )
        if (!file.exists()) {
            file.mkdirs()
        }
        file.absolutePath
    } catch (e: Exception) {
        this.filesDir.absolutePath
    }
}

fun File.getDuration(): String {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(this.absolutePath)
    val durationInMillis = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt() ?: 0
    retriever.release()
    return durationInMillis.toLong().milliSecFormat()
}

fun File.getSize(): String {
    val sizeInBytes = this.length()
    val sizeInKB = sizeInBytes / 1024.0

    return when {
        sizeInKB >= 1048576.0 -> {
            String.format("%.2f GB", sizeInKB / 1024.0 / 1024.0)
        }
        sizeInKB >= 1024.0 -> {
            String.format("%.2f MB", sizeInKB / 1024.0)
        }
        else -> {
            String.format("%.2f KB", sizeInKB)
        }
    }
}

