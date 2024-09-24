package com.example.voicechanger.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.voicechanger.R
import com.example.voicechanger.base.fragment.BaseFragmentNotRequireViewModel
import com.example.voicechanger.databinding.FragmentAudioPlayerBinding
import com.example.voicechanger.dialog.RingtoneDialog
import com.example.voicechanger.model.AudioModel
import com.example.voicechanger.navigation.AppNavigation
import com.example.voicechanger.utils.Constants.ARG_AUDIO_MODEL
import com.example.voicechanger.utils.Constants.DIRECTORY
import com.example.voicechanger.utils.Constants.Fragments.AUDIO_LIST_FRAGMENT
import com.example.voicechanger.utils.setOnSafeClickListener
import com.example.voicechanger.utils.shareFile
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlayerFragment : BaseFragmentNotRequireViewModel<FragmentAudioPlayerBinding>(R.layout.fragment_audio_player) {
    private var audioModel: AudioModel? = null
    private var player: ExoPlayer? = null
    private var directory: String = ""

    @Inject
    lateinit var appNavigation: AppNavigation

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        getData()

        initPlayer()

        initToolbar()
    }

    override fun onBack() {
        super.onBack()

        appNavigation.navigateUp()
    }

    private fun getData() {
        arguments?.let {
            audioModel = it.getParcelable(ARG_AUDIO_MODEL)
            directory = it.getString(DIRECTORY, "")
        }
    }

    private fun initToolbar() {
        binding.ivBack.isVisible = directory == AUDIO_LIST_FRAGMENT
    }

    private fun initPlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.keepScreenOn = true
        binding.playerView.player = player

        audioModel?.let {
            binding.playerView.findViewById<TextView>(R.id.tv_name).text = it.fileName
            binding.playerView.findViewById<TextView>(R.id.tv_detail).text = getString(R.string.audio_attr, it.duration, it.size)
            val mediaItem = MediaItem.fromUri(Uri.parse(it.path))
            player?.setMediaItem(mediaItem)
            player?.repeatMode = ExoPlayer.REPEAT_MODE_ALL
            player?.prepare()
            player?.play()
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.playerView.findViewById<ImageView>(R.id.exo_pause).visibility = View.VISIBLE
        binding.playerView.findViewById<ImageView>(R.id.exo_play).visibility = View.GONE

        binding.playerView.findViewById<ImageView>(R.id.exo_volume).setOnSafeClickListener {
            if (player?.volume == 0f) {
                player?.volume = 1f
                binding.playerView.findViewById<ImageView>(R.id.exo_volume).setImageResource(R.mipmap.ic_volume)
            } else {
                player?.volume = 0f
                binding.playerView.findViewById<ImageView>(R.id.exo_volume).setImageResource(R.mipmap.ic_mute)
            }
        }

        binding.playerView.findViewById<ImageView>(R.id.reset).setOnSafeClickListener {
            player?.seekTo(0)
        }

        binding.playerView.findViewById<ImageView>(R.id.exo_play).setOnSafeClickListener {
            player?.play()
            binding.playerView.findViewById<ImageView>(R.id.exo_pause).visibility = View.GONE
        }

        binding.playerView.findViewById<ImageView>(R.id.exo_pause).setOnSafeClickListener {
            player?.pause()
            binding.playerView.findViewById<ImageView>(R.id.exo_play).visibility = View.VISIBLE
            binding.playerView.findViewById<ImageView>(R.id.exo_pause).visibility = View.GONE
        }

        binding.ivBack.setOnSafeClickListener {
            appNavigation.navigateUp()
        }

        binding.llSetRingtone.setOnSafeClickListener {
            audioModel?.path?.let { filePath ->
                RingtoneDialog(filePath).show(parentFragmentManager, "SetAsRingtoneDialog")
            }
        }

        binding.tvShare.setOnSafeClickListener {
            audioModel?.path?.let { filePath ->
                requireContext().shareFile(filePath)
            }
        }

        binding.llReRecord.setOnSafeClickListener {
            appNavigation.openAudioPlayerToRecordingScreen()
        }

        binding.ivHome.setOnSafeClickListener {
            appNavigation.openAudioPlayerToHomeScreen()
        }
    }

    private fun pauseMusicPlayer() {
        player?.let {
            it.playWhenReady = false
            it.playbackState
        }
    }

    override fun onPause() {
        super.onPause()
        pauseMusicPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.let {
            it.stop()
            it.release()
        }
    }
}