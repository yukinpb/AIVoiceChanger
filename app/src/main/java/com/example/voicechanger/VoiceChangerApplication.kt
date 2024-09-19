package com.example.voicechanger

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.voicechanger.base.BaseApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VoiceChangerApplication : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        createChannelNotification(this)
    }

    private fun createChannelNotification(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "channel_voice_changer"
        const val PERMISSION_TOKEN: Int = 1212
        val ALL_PERMISSIONS_LIST: Array<String> = getRequiredPermissions().toTypedArray()

        fun hasPermissions(context: Context, vararg permissions: String): Boolean {
            return permissions.all { ContextCompat.checkSelfPermission(context, it) == 0 }
        }

        private fun getRequiredPermissions(): List<String> {
            val permissions = mutableListOf<String>()
            val sdkInt = Build.VERSION.SDK_INT
            if (sdkInt >= 33) {
                permissions.add("android.permission.READ_MEDIA_AUDIO")
                permissions.add("android.permission.POST_NOTIFICATIONS")
            } else if (sdkInt >= 29) {
                permissions.add("android.permission.WRITE_EXTERNAL_STORAGE")
                permissions.add("android.permission.READ_EXTERNAL_STORAGE")
            }
            return permissions
        }
    }
}