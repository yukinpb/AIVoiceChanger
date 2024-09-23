package com.example.voicechanger.dialog

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.voicechanger.databinding.DialogRingtoneBinding
import com.example.voicechanger.utils.setOnSafeClickListener
import com.example.voicechanger.utils.toast
import java.io.File
import java.io.IOException
import java.util.Locale

enum class MobileState {
    STATE_RINGTONE,
    STATE_ALARM,
    STATE_NOTIFICATION
}

class RingtoneDialog(
    private val audioFilePath: String
) : DialogFragment() {
    private var binding: DialogRingtoneBinding? = null
    private var state: MobileState = MobileState.STATE_RINGTONE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding = DialogRingtoneBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.tvCancel?.setOnSafeClickListener {
            dismiss()
        }

        binding?.rbRingtones?.setOnSafeClickListener {
            onSelect(MobileState.STATE_RINGTONE)
        }

        binding?.rbAlarm?.setOnSafeClickListener {
            onSelect(MobileState.STATE_ALARM)
        }

        binding?.rbNotification?.setOnSafeClickListener {
            onSelect(MobileState.STATE_NOTIFICATION)
        }

        binding?.tvSet?.setOnSafeClickListener {
            setRingtone()
            dismiss()
        }
    }

    private fun onSelect(state: MobileState) {
        this.state = state
        binding?.rbRingtones?.isChecked = state == MobileState.STATE_RINGTONE
        binding?.rbAlarm?.isChecked = state == MobileState.STATE_ALARM
        binding?.rbNotification?.isChecked = state == MobileState.STATE_NOTIFICATION
    }

    private fun setRingtone() {
        val file = File(audioFilePath)
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
                            getRingtoneType(),
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

    private fun getRingtoneType(): Int {
        return when (state) {
            MobileState.STATE_RINGTONE -> RingtoneManager.TYPE_RINGTONE
            MobileState.STATE_ALARM -> RingtoneManager.TYPE_ALARM
            MobileState.STATE_NOTIFICATION -> RingtoneManager.TYPE_NOTIFICATION
        }
    }


    override fun onResume() {
        super.onResume()

        val layoutParams = dialog?.window?.attributes
        layoutParams?.width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = layoutParams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}