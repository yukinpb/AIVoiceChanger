package com.example.voicechanger.viewModel

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.voicechanger.base.BaseViewModel
import com.example.voicechanger.utils.getVoiceEffectDirPath
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : BaseViewModel() {
    var player: ExoPlayer? = null
    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private var voiceUrl: String = ""

    private val outputDir =
        "${context.getVoiceEffectDirPath()}/ai_tts_${System.currentTimeMillis()}.wav"

    private val _playbackSpeed = MutableLiveData(1.0f)
    val playbackSpeed: LiveData<Float> = _playbackSpeed

    private var hasPlayEnd = false

    private val playbackSpeeds = listOf(0.5f, 1.0f, 1.5f, 2.0f)

    fun setVoiceUrl(voiceUrl: String) {
        this.voiceUrl = voiceUrl
    }

    fun initPlayer(player: ExoPlayer) {
        this.player = player

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    _isPlaying.value = false
                    hasPlayEnd = true
                }
            }
        })
    }

    fun downloadAVoice() {
        viewModelScope.launch {
            try {
                val downloadManager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
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

    private fun setPlaybackSpeed(speed: Float) {
        player?.let {
            it.setPlaybackSpeed(speed)
            _playbackSpeed.value = speed
        }
    }

    fun changeSpeed() {
        val currentSpeed = _playbackSpeed.value ?: 1.0f
        val nextSpeed =
            playbackSpeeds[(playbackSpeeds.indexOf(currentSpeed) + 1) % playbackSpeeds.size]
        setPlaybackSpeed(nextSpeed)
    }

    fun play() {
        if (hasPlayEnd) {
            player?.seekTo(0)
            hasPlayEnd = false
        }
        player?.play()
        _isPlaying.value = true
    }

    fun pause() {
        player?.pause()
        _isPlaying.value = false
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}