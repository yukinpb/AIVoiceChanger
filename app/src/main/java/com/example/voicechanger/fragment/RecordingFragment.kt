package com.example.voicechanger.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.voicechanger.R
import com.example.voicechanger.base.fragment.BaseFragment
import com.example.voicechanger.databinding.FragmentRecordingBinding
import com.example.voicechanger.dialog.ConfirmDialog
import com.example.voicechanger.navigation.AppNavigation
import com.example.voicechanger.service.VoiceRecordingService
import com.example.voicechanger.utils.Constants.ARG_AUDIO_PATH
import com.example.voicechanger.utils.setOnSafeClickListener
import com.example.voicechanger.viewModel.RecordingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

enum class RecordAudioState {
    STATE_PREPARE,
    STATE_START,
    STATE_PAUSE
}

@AndroidEntryPoint
class RecordingFragment :
    BaseFragment<FragmentRecordingBinding, RecordingViewModel>(R.layout.fragment_recording) {

    private var stateAudio: RecordAudioState = RecordAudioState.STATE_PREPARE

    @Inject
    lateinit var appNavigation: AppNavigation

    override fun getVM(): RecordingViewModel {
        val viewModel: RecordingViewModel by viewModels()
        return viewModel
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        binding.toolbar.tvTitle.setText(R.string.record_voice)

        setupController()

        connectService()
    }

    override fun onBack() {
        super.onBack()

        onBackClick()
    }

    override fun onResume() {
        super.onResume()

        setupController()
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.toolbar.ivBack.setOnSafeClickListener {
            onBackClick()
        }

        binding.ivReset.setOnSafeClickListener {
            showDialogNotSaved(
                isRecording = true,
                isBack = false,
                title = getString(R.string.audio_not_saved),
                content = getString(R.string.audio_has_not_been_saved),
                negative = getString(R.string.cancel),
                positive = getString(R.string.reset)
            )
        }

        binding.icStart.setOnSafeClickListener {
            startRecordAudio()
        }

        binding.imgRecord.setOnSafeClickListener {
            when(stateAudio) {
                RecordAudioState.STATE_PREPARE -> {
                    startRecordAudio()
                }
                RecordAudioState.STATE_START -> {
                    pauseRecordAudio()
                }
                RecordAudioState.STATE_PAUSE -> {
                    continueRecordAudio()
                }
            }
        }

        binding.imgStop.setOnSafeClickListener {
            stopRecordAudio()
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        getVM().elapsedTime.observe(this) { time ->
            binding.txtStartRecord.text = time
        }
    }

    private fun connectService() {
        context?.let {
            getVM().connectService(Intent(it, VoiceRecordingService::class.java))
        }
    }

    private fun startRecordAudio() {
        stateAudio = RecordAudioState.STATE_START
        setupController()
        startAnim()
        getVM().startRecording()
    }

    private fun pauseRecordAudio() {
        stateAudio = RecordAudioState.STATE_PAUSE
        setupController()
        getVM().pauseRecording()
    }

    private fun continueRecordAudio() {
        stateAudio = RecordAudioState.STATE_START
        setupController()
        getVM().resumeRecording()
    }

    private fun stopRecordAudio() {
        stateAudio = RecordAudioState.STATE_PREPARE
        setupController()
        stopAnim()
        getVM().stopRecording()

        val bundle = Bundle().apply {
            putString(ARG_AUDIO_PATH, getVM().recordingPath)
        }
        appNavigation.openRecordingToChangeEffectScreen(bundle)
    }

    private fun resetRecordAudio() {
        stateAudio = RecordAudioState.STATE_PREPARE
        setupController()
        stopAnim()
        getVM().resetRecording()
    }

    private fun setupController() {
        when (stateAudio) {
            RecordAudioState.STATE_PREPARE -> {
                binding.icStart.isClickable = true
                binding.icStart.setImageResource(R.mipmap.ic_start_record)
                binding.rlyBottom.visibility = View.GONE
                binding.imgRecord.visibility = View.GONE
                binding.txtStartRecord.setText(R.string.start_record)
                binding.txtExtra.visibility = View.VISIBLE
            }

            RecordAudioState.STATE_START -> {
                binding.icStart.isClickable = false
                binding.icStart.setImageResource(R.mipmap.ic)
                binding.imgRecord.setImageResource(R.mipmap.ic_pause)
                binding.rlyBottom.visibility = View.VISIBLE
                binding.imgRecord.visibility = View.VISIBLE
                binding.txtExtra.visibility = View.GONE
            }

            RecordAudioState.STATE_PAUSE -> {
                binding.icStart.isClickable = false
                binding.icStart.setImageResource(R.mipmap.ic)
                binding.imgRecord.setImageResource(R.mipmap.ic_play)
                binding.rlyBottom.visibility = View.VISIBLE
                binding.imgRecord.visibility = View.VISIBLE
                binding.txtExtra.visibility = View.GONE
            }
        }
    }

    private fun onBackClick() {
        if (binding.icStart.isClickable) {
            appNavigation.openRecordingToHomeScreen()
        } else {
            showDialogNotSaved(
                isRecording = binding.ivReset.isShown,
                isBack = true,
                title = getString(R.string.audio_not_saved),
                content = getString(R.string.audio_has_not_been_saved),
                negative = getString(R.string.cancel),
                positive = getString(R.string.exit)
            )
        }
    }

    private fun showDialogNotSaved(
        isRecording: Boolean,
        isBack: Boolean,
        title: String,
        content: String,
        negative: String,
        positive: String
    ) {
        if (isRecording) {
            ConfirmDialog(
                title = title,
                content = content,
                negative = negative,
                positive = positive,
                onNegative = {
                },
                onPositive = {
                    resetRecordAudio()
                    if (isBack) {
                        appNavigation.openRecordingToHomeScreen()
                    }
                }).show(childFragmentManager, "ConfirmDialog")
        } else {
            stopRecordAudio()
            appNavigation.openRecordingToHomeScreen()
        }
    }

    private fun startAnim() {
        binding.recordLottie.visibility = View.VISIBLE
    }

    private fun stopAnim() {
        binding.recordLottie.visibility = View.GONE
    }
}