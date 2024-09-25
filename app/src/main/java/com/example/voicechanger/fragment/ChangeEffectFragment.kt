package com.example.voicechanger.fragment

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.voicechanger.R
import com.example.voicechanger.adapter.EffectAdapter
import com.example.voicechanger.adapter.FragmentPagerAdapter
import com.example.voicechanger.base.fragment.BaseFragment
import com.example.voicechanger.databinding.FragmentChangeEffectBinding
import com.example.voicechanger.dialog.ConfirmDialog
import com.example.voicechanger.dialog.SaveDialog
import com.example.voicechanger.navigation.AppNavigation
import com.example.voicechanger.utils.Constants.ARG_AUDIO_MODEL
import com.example.voicechanger.utils.Constants.ARG_AUDIO_PATH
import com.example.voicechanger.utils.Constants.DIRECTORY
import com.example.voicechanger.utils.Constants.Fragments.CHANGE_EFFECT_FRAGMENT
import com.example.voicechanger.utils.EffectType
import com.example.voicechanger.utils.milliSecFormat
import com.example.voicechanger.utils.setOnSafeClickListener
import com.example.voicechanger.utils.toast
import com.example.voicechanger.viewModel.ChangeEffectViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

enum class PlayAudioState {
    STATE_PLAY,
    STATE_PAUSE
}

@AndroidEntryPoint
class ChangeEffectFragment :
    BaseFragment<FragmentChangeEffectBinding, ChangeEffectViewModel>(R.layout.fragment_change_effect) {
    private var stateAudio: PlayAudioState = PlayAudioState.STATE_PAUSE

    @Inject
    lateinit var appNavigation: AppNavigation

    private lateinit var effectAdapter : EffectAdapter

    override fun getVM(): ChangeEffectViewModel {
        val viewModel: ChangeEffectViewModel by activityViewModels()
        return viewModel
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        getData()

        initToolbar()

        initMainView()

        playAudio()

        setupProgressBar()
    }

    private fun getData() {
        val path = arguments?.getString(ARG_AUDIO_PATH)

        path?.let {
            getVM().setRecordingPath(path)
            getVM().getTypeEffectList()
        }
    }

    private fun initToolbar() {
        binding.toolbar.ivDone.visibility = View.VISIBLE
        binding.toolbar.tvTitle.text = getString(R.string.voice_effects)
    }

    private fun initMainView() {
        effectAdapter = EffectAdapter(requireContext()) {
            binding.viewPager.setCurrentItem(it, true)
        }

        effectAdapter.submitList(getVM().getTypeEffectList())

        binding.rvAudioEffect.apply {
            adapter = effectAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }

        val audioEffectFragmentList = listOf(
            AudioEffectFragment.newInstance(EffectType.ALL),
            AudioEffectFragment.newInstance(EffectType.SCARY),
            AudioEffectFragment.newInstance(EffectType.OTHER),
            AudioEffectFragment.newInstance(EffectType.PEOPLE),
            AudioEffectFragment.newInstance(EffectType.ROBOT)
        )
        val viewpagerAdapter = activity?.let { FragmentPagerAdapter(it, audioEffectFragmentList) }
        binding.viewPager.adapter = viewpagerAdapter
        binding.viewPager.setCurrentItem(0, false)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                effectAdapter.selectItem(position)
            }
        })
    }

    override fun onBack() {
        super.onBack()

        getVM().stopAudio()
        getVM().deleteAllTempFiles()
        appNavigation.navigateUp()
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.playerView.exoVolume.setOnSafeClickListener {
            getVM().changeVolume()
        }

        binding.playerView.exoPlay.setOnSafeClickListener {
            when (stateAudio) {
                PlayAudioState.STATE_PLAY -> {
                    pauseAudio()
                }
                PlayAudioState.STATE_PAUSE -> {
                    resumeAudio()
                }
            }
        }

        binding.playerView.imgReset.setOnSafeClickListener {
            resetAudio()
        }

        binding.toolbar.ivBack.setOnSafeClickListener {
            onBack()
        }

        binding.toolbar.ivDone.setOnSafeClickListener {
            showSaveDialog()
        }

        binding.playerView.btnSpeed.setOnSafeClickListener {
            getVM().changeSpeed()
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        getVM().progress.observe(this) { progress ->
            binding.playerView.seekbar.progress = progress
            binding.playerView.exoPosition1.text = progress.toLong().milliSecFormat()
        }

        getVM().isVolumeOn.observe(this) { isVolumeOn ->
            if (isVolumeOn) {
                binding.playerView.exoVolume.setImageResource(R.mipmap.ic_volume)
            } else {
                binding.playerView.exoVolume.setImageResource(R.mipmap.ic_mute)
            }
        }

        getVM().isPlaying.observe(this) { isPlaying ->
            if (isPlaying) {
                binding.playerView.exoPlay.setImageResource(R.mipmap.ic_pause)
            } else {
                stateAudio = PlayAudioState.STATE_PAUSE
                binding.playerView.exoPlay.setImageResource(R.mipmap.ic_play)
            }
        }

        getVM().maxDuration.observe(this) { maxDuration ->
            binding.playerView.exoDuration.text = maxDuration.toLong().milliSecFormat()
            binding.playerView.seekbar.max = maxDuration
        }

        getVM().playbackSpeed.observe(this) { speed ->
            binding.playerView.btnSpeed.text = getString(R.string.speed, speed)
        }
    }

    private fun showSaveDialog() {
        SaveDialog(
            onNegative = {},
            onPositive = { fileName ->
                saveAudio(fileName)
            }
        ).show(childFragmentManager, "SaveDialog")
    }

    private fun saveAudio(fileName: String) {
        getVM().setFinalFileName(fileName)
        if (getVM().saveAudio(showConfirmDialog = ::showFileExistDialog)) {
            getVM().stopAudio()
            getVM().deleteAllTempFiles()
            goToPlayerScreen()
        }
    }

    private fun showFileExistDialog(fileName: String, onConfirm: () -> Unit) {
        ConfirmDialog(
            title = getString(R.string.rename),
            content = getString(R.string.file_already_exists, fileName),
            negative = getString(R.string.cancel),
            positive = getString(R.string.ok),
            onNegative = {
                requireContext().toast(getString(R.string.save_audio_failed))
            },
            onPositive = {
                onConfirm()
            }
        ).show(parentFragmentManager, ConfirmDialog::class.java.simpleName)
    }

    private fun goToPlayerScreen() {
        val bundle = Bundle().apply {
            putParcelable(ARG_AUDIO_MODEL, getVM().getAudioSaved())
            putString(DIRECTORY, CHANGE_EFFECT_FRAGMENT)
        }

        appNavigation.openChangeEffectToPlayerScreen(bundle)
    }

    private fun playAudio() {
        stateAudio = PlayAudioState.STATE_PLAY
        getVM().playAudio()
        binding.playerView.exoPlay.setImageResource(R.mipmap.ic_pause)
    }

    private fun pauseAudio() {
        stateAudio = PlayAudioState.STATE_PAUSE
        getVM().pauseAudio()
        binding.playerView.exoPlay.setImageResource(R.mipmap.ic_play)
    }

    private fun resumeAudio() {
        stateAudio = PlayAudioState.STATE_PLAY
        getVM().resumeAudio()
        binding.playerView.exoPlay.setImageResource(R.mipmap.ic_pause)
    }

    private fun resetAudio() {
        stateAudio = PlayAudioState.STATE_PLAY
        getVM().resetAudio()
        binding.playerView.exoPlay.setImageResource(R.mipmap.ic_pause)
        binding.playerView.seekbar.progress = 0
    }

    private fun setupProgressBar() {
        binding.playerView.seekbar.progress = 0

        binding.playerView.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    getVM().seekTo(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // No action needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // No action needed
            }
        })
    }

}