package com.example.voicechanger.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.voicechanger.R
import com.example.voicechanger.base.fragment.BaseFragment
import com.example.voicechanger.databinding.FragmentEditAudioBinding
import com.example.voicechanger.dialog.ConfirmDialog
import com.example.voicechanger.dialog.SaveDialog
import com.example.voicechanger.navigation.AppNavigation
import com.example.voicechanger.utils.Constants.ARG_AUDIO_MODEL
import com.example.voicechanger.utils.Constants.ARG_AUDIO_PATH
import com.example.voicechanger.utils.Constants.DIRECTORY
import com.example.voicechanger.utils.Constants.Fragments.CHANGE_EFFECT_FRAGMENT
import com.example.voicechanger.utils.milliSecFormat
import com.example.voicechanger.utils.setOnSafeClickListener
import com.example.voicechanger.utils.toast
import com.example.voicechanger.viewModel.EditAudioViewModel
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.masoudss.lib.SeekBarOnProgressChanged
import com.masoudss.lib.WaveformSeekBar
import com.masoudss.lib.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditAudioFragment : BaseFragment<FragmentEditAudioBinding, EditAudioViewModel>(R.layout.fragment_edit_audio) {

    @Inject
    lateinit var appNavigation: AppNavigation

    private var stateAudio: PlayAudioState = PlayAudioState.STATE_PAUSE

    private var path: String = ""

    private var duration = 0f

    private var startTime = 0f

    private var endTime = 0f

    private var direction = ""

    override fun getVM(): EditAudioViewModel {
        val viewModel: EditAudioViewModel by viewModels()
        return viewModel
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        getData()

        initToolbar()

        playAudio()
    }

    private fun getData() {
        path = arguments?.getString(ARG_AUDIO_PATH) ?: ""
        direction = arguments?.getString(DIRECTORY) ?: ""

        getVM().setRecordingPath(path)
    }

    private fun initToolbar() {
        binding.toolbar.ivDone.visibility = View.VISIBLE
        binding.toolbar.tvTitle.text = getString(R.string.trim_audio_1)
    }

    override fun onBack() {
        super.onBack()

        getVM().stopAudio()
        getVM().deleteAllTempFiles()
        appNavigation.navigateUp()
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnPlay.setOnSafeClickListener {
            when (stateAudio) {
                PlayAudioState.STATE_PLAY -> {
                    pauseAudio()
                }
                PlayAudioState.STATE_PAUSE -> {
                    resumeAudio()
                }
            }
        }

        binding.toolbar.ivBack.setOnSafeClickListener {
            onBack()
        }

        binding.toolbar.ivDone.setOnSafeClickListener {
            showSaveDialog()
        }

        binding.btnTrim.setOnSafeClickListener {
            getVM().trimAudio(startTime.toLong().milliSecFormat(), endTime.toLong().milliSecFormat())
        }

        binding.btnCut.setOnSafeClickListener {
            getVM().cutAudio(startTime.toLong().milliSecFormat(), endTime.toLong().milliSecFormat())
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        getVM().progress.observe(this) { progress ->
            binding.waveformSeekBar.progress = progress.toFloat()
        }

        getVM().isPlaying.observe(this) { isPlaying ->
            if (isPlaying) {
                binding.btnPlay.setImageResource(R.mipmap.ic_pause)
            } else {
                binding.btnPlay.setImageResource(R.mipmap.ic_play)
            }
        }

        getVM().maxDuration.observe(this) { maxDuration ->
            if (maxDuration != 0) {
                duration = maxDuration.toFloat()
                path = getVM().getCurrentFileNamePlay()
                setupProgressBar()
            }
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
        binding.btnPlay.setImageResource(R.mipmap.ic_pause)
    }

    private fun pauseAudio() {
        stateAudio = PlayAudioState.STATE_PAUSE
        getVM().pauseAudio()
        binding.btnPlay.setImageResource(R.mipmap.ic_play)
    }

    private fun resumeAudio() {
        stateAudio = PlayAudioState.STATE_PLAY
        getVM().resumeAudio()
        binding.btnPlay.setImageResource(R.mipmap.ic_pause)
    }

    private fun setupProgressBar() {
        startTime = 0f
        endTime = duration

        binding.tvStart.text = 0L.milliSecFormat()
        binding.tvEnd.text = duration.toLong().milliSecFormat()

        binding.waveformSeekBar.maxProgress = duration

        binding.waveformSeekBar.apply {
            progress = 0F
            markerWidth = Utils.dp(requireContext(), 3)
            onProgressChanged = object : SeekBarOnProgressChanged {
                override fun onProgressChanged(
                    waveformSeekBar: WaveformSeekBar,
                    progress: Float,
                    fromUser: Boolean
                ) {
                    if (fromUser)
                        return
                }
            }
        }

        binding.waveformSeekBar.setSampleFrom(path)

        binding.tvStart.text = startTime.toLong().milliSecFormat()
        binding.tvEnd.text = endTime.toLong().milliSecFormat()

        binding.waveformSeekBar.marker = hashMapOf(0f to "00:00", duration to duration.toLong().milliSecFormat())

        binding.sbRange5.setRange(0f, duration)

        binding.sbRange5.setValue(0f, duration)

        binding.btnCut.isEnabled = false

        binding.sbRange5.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(rangeSeekBar: RangeSeekBar, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                binding.btnCut.isEnabled = leftValue != 0f || rightValue != duration

                startTime = (leftValue / binding.waveformSeekBar.maxProgress) * duration
                endTime = (rightValue / binding.waveformSeekBar.maxProgress) * duration

                binding.waveformSeekBar.marker = hashMapOf(
                    leftValue to startTime.toLong().milliSecFormat(),
                    rightValue to endTime.toLong().milliSecFormat()
                )

                binding.tvStart.text = startTime.toLong().milliSecFormat()
                binding.tvEnd.text = endTime.toLong().milliSecFormat()
            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }

        })
    }

}