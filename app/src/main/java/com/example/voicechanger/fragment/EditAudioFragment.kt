package com.example.voicechanger.fragment

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.voicechanger.R
import com.example.voicechanger.base.fragment.BaseFragment
import com.example.voicechanger.databinding.FragmentEditAudioBinding
import com.example.voicechanger.dialog.ConfirmDialog
import com.example.voicechanger.dialog.MobileState
import com.example.voicechanger.dialog.SaveDialog
import com.example.voicechanger.fragment.AudioListFragment.Companion.DIRECTORY
import com.example.voicechanger.fragment.AudioListFragment.Companion.RINGTONE_MAKER_FRAGMENT
import com.example.voicechanger.fragment.ChangeEffectFragment.Companion.ARG_AUDIO_MODEL
import com.example.voicechanger.fragment.ChangeEffectFragment.Companion.CHANGE_EFFECT_FRAGMENT
import com.example.voicechanger.fragment.RecordingFragment.Companion.ARG_AUDIO_PATH
import com.example.voicechanger.navigation.AppNavigation
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
import java.io.File
import java.io.IOException
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

        binding.llSet.isVisible = direction == RINGTONE_MAKER_FRAGMENT
    }

    private fun initToolbar() {
        binding.toolbar.ivDone.visibility = View.VISIBLE
        if (direction == RINGTONE_MAKER_FRAGMENT) {
            binding.toolbar.tvTitle.text = getString(R.string.ringtone_maker)
        } else {
            binding.toolbar.tvTitle.text = getString(R.string.trim_audio_1)
        }
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

        binding.llSetRingtone.setOnSafeClickListener {
            setRingtone(MobileState.STATE_RINGTONE)
        }

        binding.llSetAlarm.setOnSafeClickListener {
            setRingtone(MobileState.STATE_ALARM)
        }

        binding.llSetNotification.setOnSafeClickListener {
            setRingtone(MobileState.STATE_NOTIFICATION)
        }
    }

    private fun setRingtone(state: MobileState) {
        val file = File(path)
        if (!file.exists()) return

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.TITLE, "My Ringtone")
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
            put(MediaStore.Audio.Media.IS_RINGTONE, true)
            put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
            put(MediaStore.Audio.Media.IS_ALARM, false)
            put(MediaStore.Audio.Media.IS_MUSIC, false)
        }

        val context: Context = requireContext()

        val contentUri = MediaStore.Audio.Media.getContentUriForPath(file.absolutePath)

        if (contentUri != null) {
            context.contentResolver.delete(
                contentUri,
                MediaStore.MediaColumns.TITLE + "=?",
                arrayOf("My Ringtone")
            )

            val newUri = context.contentResolver.insert(contentUri, values)

            if (newUri != null) {
                if (Settings.System.canWrite(context)) {
                    try {
                        RingtoneManager.setActualDefaultRingtoneUri(
                            context,
                            getRingtoneType(state),
                            newUri
                        )
                        val stateText = when (state) {
                            MobileState.STATE_RINGTONE -> "Ringtone"
                            MobileState.STATE_ALARM -> "Alarm"
                            MobileState.STATE_NOTIFICATION -> "Notification"
                        }
                        context.toast("Set as $stateText successfully")
                    } catch (e: IOException) {
                        e.printStackTrace()
                        context.toast("Device does not support this feature")
                    }
                } else {
                    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                        data = Uri.parse("package:${context.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            } else {
                context.toast("Failed to add ringtone to MediaStore")
            }
        }
    }

    private fun getRingtoneType(state: MobileState): Int {
        return when (state) {
            MobileState.STATE_RINGTONE -> RingtoneManager.TYPE_RINGTONE
            MobileState.STATE_ALARM -> RingtoneManager.TYPE_ALARM
            MobileState.STATE_NOTIFICATION -> RingtoneManager.TYPE_NOTIFICATION
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

        binding.sbRange5.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(rangeSeekBar: RangeSeekBar, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
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