package com.example.voicechanger.viewModel

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.voicechanger.base.BaseViewModel
import com.example.voicechanger.utils.getVoiceEffectDirPath
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class AudioPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : BaseViewModel() {
    private fun downloadAndSaveVoice(voiceUrl: String) {
        viewModelScope.launch {
            try {
                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val uri = Uri.parse(voiceUrl)
                val request = DownloadManager.Request(uri)
                    .setTitle("Downloading Voice")
                    .setDescription("Downloading voice file using DownloadManager")
                    .setDestinationUri(Uri.fromFile(File(outputDir)))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                downloadManager.enqueue(request)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val outputDir =
        "${context.getVoiceEffectDirPath()}/ai_tts_${System.currentTimeMillis()}.wav"

}