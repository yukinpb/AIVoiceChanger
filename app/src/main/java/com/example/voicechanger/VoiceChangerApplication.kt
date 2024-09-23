package com.example.voicechanger

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.voicechanger.base.BaseApplication
import com.example.voicechanger.fragment.LocaleHelper
import com.example.voicechanger.pref.AppPreferences
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class VoiceChangerApplication : BaseApplication() {

    @Inject
    lateinit var appPreferences: AppPreferences
    override fun onCreate() {
        super.onCreate()
        setAppLocale()
        createChannelNotification(this)
    }

    private fun setAppLocale() {
        runBlocking {
            val languageCode = appPreferences.getLanguage().firstOrNull()
            languageCode?.let {
                LocaleHelper.setLocale(this@VoiceChangerApplication, languageCode)
            }
        }
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
        val ALL_PERMISSIONS_LIST: Array<String> = getRequiredPermissions().toTypedArray()

        fun hasPermissions(context: Context, vararg permissions: String): Boolean {
            return permissions.all { ContextCompat.checkSelfPermission(context, it) == 0 }
        }

        private fun getRequiredPermissions(): List<String> {
            val permissions = mutableListOf<String>()
            val sdkInt = Build.VERSION.SDK_INT
            if (sdkInt >= 33) {
                permissions.add("android.permission.RECORD_AUDIO")
                permissions.add("android.permission.READ_MEDIA_AUDIO")
                permissions.add("android.permission.POST_NOTIFICATIONS")
            } else if (sdkInt >= 29) {
                permissions.add("android.permission.WRITE_EXTERNAL_STORAGE")
                permissions.add("android.permission.READ_EXTERNAL_STORAGE")
                permissions.add("android.permission.RECORD_AUDIO")
            }
            return permissions
        }
    }
}