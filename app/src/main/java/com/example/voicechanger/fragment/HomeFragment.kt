package com.example.voicechanger.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.view.GravityCompat
import com.example.voicechanger.R
import com.example.voicechanger.base.fragment.BaseFragmentNotRequireViewModel
import com.example.voicechanger.databinding.FragmentHomeBinding
import com.example.voicechanger.fragment.AudioListFragment.Companion.DIRECTORY
import com.example.voicechanger.fragment.AudioListFragment.Companion.MERGE_AUDIO_FRAGMENT
import com.example.voicechanger.fragment.AudioListFragment.Companion.MY_VOICE_FRAGMENT
import com.example.voicechanger.fragment.AudioListFragment.Companion.OPEN_FILE_FRAGMENT
import com.example.voicechanger.fragment.AudioListFragment.Companion.RINGTONE_MAKER_FRAGMENT
import com.example.voicechanger.navigation.AppNavigation
import com.example.voicechanger.utils.setOnSafeClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragmentNotRequireViewModel<FragmentHomeBinding>(R.layout.fragment_home) {

    @Inject
    lateinit var appNavigation: AppNavigation

    override fun setOnClick() {
        super.setOnClick()

        toolbarAction()

        mainAction()
    }

    private fun toolbarAction() {
        binding.toolbar.ivMenu.setOnSafeClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.layoutContent.llLanguage.setOnSafeClickListener {
            closeDrawer()
            // TODO: Open language screen
        }

        binding.layoutContent.txtCancel.setOnSafeClickListener {
            closeDrawer()
        }

        binding.layoutContent.llRateUs.setOnSafeClickListener {
            closeDrawer()
            // TODO: Open rate us in app store
        }

        binding.layoutContent.llShare.setOnSafeClickListener {
            closeDrawer()
            shareApp()
        }

        binding.layoutContent.llPolicy.setOnSafeClickListener {
            closeDrawer()
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")))
            } catch (e: Exception) {
                Log.e("MTAG", "privacy  Error:$e")
            }
        }
    }

    private fun mainAction() {
        binding.llRecord.setOnSafeClickListener {
            appNavigation.openHomeToRecordingScreen()
        }

        binding.llOpenFile.setOnSafeClickListener {
            appNavigation.openHomeToAudioListScreen(Bundle().apply {
                putString(DIRECTORY, OPEN_FILE_FRAGMENT)
            })
        }

        binding.llTextAudio.setOnSafeClickListener {
            appNavigation.openHomeToTextToAudioScreen()
        }

        binding.llMyVoice.setOnSafeClickListener {
            appNavigation.openHomeToAudioListScreen(Bundle().apply {
                putString(DIRECTORY, MY_VOICE_FRAGMENT)
            })
        }

        binding.relRingtoneMaker.setOnSafeClickListener {
            appNavigation.openHomeToAudioListScreen(Bundle().apply {
                putString(DIRECTORY, RINGTONE_MAKER_FRAGMENT)
            })
        }

        binding.trimAudio.setOnSafeClickListener {
            appNavigation.openHomeToAudioListScreen(Bundle().apply {
                putString(DIRECTORY, RINGTONE_MAKER_FRAGMENT)
            })
        }

        binding.mergeAudio.setOnSafeClickListener {
            appNavigation.openHomeToAudioListScreen(Bundle().apply {
                putString(DIRECTORY, MERGE_AUDIO_FRAGMENT)
            })
        }
    }

    private fun closeDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun shareApp() {
        try {
            val intent = Intent("android.intent.action.SEND")
            intent.type = "text/plain"
            intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name))
            intent.putExtra("android.intent.extra.TEXT", resources.getString(R.string.appShare))
            startActivity(Intent.createChooser(intent, getString(R.string.share_to)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}