package com.example.voicechanger.fragment

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.voicechanger.R
import com.example.voicechanger.VoiceChangerApplication
import com.example.voicechanger.base.fragment.BaseFragmentNotRequireViewModel
import com.example.voicechanger.databinding.FragmentSlashBinding
import com.example.voicechanger.navigation.AppNavigation
import com.example.voicechanger.pref.AppPreferences
import com.example.voicechanger.utils.getVoiceRecordDirPath
import com.example.voicechanger.utils.setOnSafeClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class SlashFragment : BaseFragmentNotRequireViewModel<FragmentSlashBinding>(R.layout.fragment_slash) {

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var appNavigation: AppNavigation

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            lifecycleScope.launch {
                if (appPreferences.isFirstTime().firstOrNull() == true) {
                    appNavigation.openSplashToLanguageScreen()
                } else {
                    appNavigation.openSplashToHomeScreen()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Please allow the permission", Toast.LENGTH_SHORT).show()
            requestPermissions()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        lifecycleScope.launch {
            binding.animationView.resumeAnimation()
            removeAllTempFile()
            delay(1500)
            requestPermissions()
        }
    }

    private fun removeAllTempFile() {
        val directory = File(requireContext().getVoiceRecordDirPath())

        if (directory.exists()) {
            directory.listFiles()?.forEach { file ->
                if (file.name.contains("_temp")) {
                    file.delete()
                }
            }
        }
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(VoiceChangerApplication.ALL_PERMISSIONS_LIST)
    }
}