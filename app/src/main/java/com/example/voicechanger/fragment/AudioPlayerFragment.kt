package com.example.voicechanger.fragment

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.voicechanger.R
import com.example.voicechanger.base.fragment.BaseFragment
import com.example.voicechanger.databinding.FragmentAudioPlayerBinding
import com.example.voicechanger.dialog.ConfirmDialog
import com.example.voicechanger.dialog.RingtoneDialog
import com.example.voicechanger.model.AudioModel
import com.example.voicechanger.navigation.AppNavigation
import com.example.voicechanger.utils.Constants.ARG_AUDIO_MODEL
import com.example.voicechanger.utils.Constants.DIRECTORY
import com.example.voicechanger.utils.Constants.Fragments.AI_VOICE_MAKER_FRAGMENT
import com.example.voicechanger.utils.Constants.Fragments.AUDIO_LIST_FRAGMENT
import com.example.voicechanger.utils.setOnSafeClickListener
import com.example.voicechanger.utils.shareFile
import com.example.voicechanger.viewModel.AudioPlayerViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlayerFragment :
    BaseFragment<FragmentAudioPlayerBinding, AudioPlayerViewModel>(R.layout.fragment_audio_player) {
    private var audioModel: AudioModel? = null
    private var player: ExoPlayer? = null
    private var directory: String = ""

    @Inject
    lateinit var appNavigation: AppNavigation

    override fun getVM(): AudioPlayerViewModel {
        val viewModel: AudioPlayerViewModel by viewModels()
        return viewModel
    }

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
            getVM().setVoiceUrl(audioModel?.path ?: "")
        }
    }

    private fun initToolbar() {
        binding.toolbar.ivBack.isVisible = directory == AUDIO_LIST_FRAGMENT
        binding.toolbar.tvTitle.text = getString(R.string.player)
        binding.toolbar.ivDownload.isVisible = directory == AI_VOICE_MAKER_FRAGMENT
    }

    private fun initPlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.keepScreenOn = true
        binding.playerView.player = player

        audioModel?.let {
            binding.playerView.findViewById<TextView>(R.id.tv_name).text = it.fileName
            binding.playerView.findViewById<TextView>(R.id.tv_detail).text =
                getString(R.string.audio_attr, it.duration, it.size)
            val mediaItem = MediaItem.fromUri(Uri.parse(it.path))
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.play()
            getVM().initPlayer(player!!)
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        getVM().isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.playerView.findViewById<ImageView>(R.id.exo_play).isVisible = !isPlaying
            binding.playerView.findViewById<ImageView>(R.id.exo_pause).isVisible = isPlaying
        }

        getVM().playbackSpeed.observe(viewLifecycleOwner) { speed ->
            binding.playerView.findViewById<TextView>(R.id.btn_speed).text =
                getString(R.string.speed, speed)
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.playerView.findViewById<ImageView>(R.id.exo_volume).setOnSafeClickListener {
            if (getVM().player?.volume == 0f) {
                getVM().player?.volume = 1f
                binding.playerView.findViewById<ImageView>(R.id.exo_volume)
                    .setImageResource(R.mipmap.ic_volume)
            } else {
                getVM().player?.volume = 0f
                binding.playerView.findViewById<ImageView>(R.id.exo_volume)
                    .setImageResource(R.mipmap.ic_mute)
            }
        }

        binding.playerView.findViewById<ImageView>(R.id.reset).setOnSafeClickListener {
            getVM().player?.seekTo(0)
        }

        binding.playerView.findViewById<ImageView>(R.id.exo_play).setOnSafeClickListener {
            getVM().play()
        }

        binding.playerView.findViewById<ImageView>(R.id.exo_pause).setOnSafeClickListener {
            getVM().pause()
        }

        binding.playerView.findViewById<TextView>(R.id.btn_speed).setOnSafeClickListener {
            getVM().changeSpeed()
        }

        binding.toolbar.ivBack.setOnSafeClickListener {
            onBack()
        }

        binding.toolbar.ivDownload.setOnSafeClickListener {
            showDialogConfirmDownload()
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

    private fun showDialogConfirmDownload() {
        ConfirmDialog(
            title = getString(R.string.download),
            content = getString(R.string.download_message),
            negative = getString(R.string.cancel),
            positive = getString(R.string.ok),
            onNegative = {},
            onPositive = {
                getVM().downloadAVoice()
            }
        ).show(parentFragmentManager, "ConfirmDialog")
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

    override fun onResume() {
        super.onResume()
        player?.playWhenReady = true
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.let {
            it.stop()
            it.release()
        }
    }
}