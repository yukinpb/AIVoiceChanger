package com.example.voicechanger.fragment

import android.os.Bundle
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

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
            startHomePage()
        } else {
            Toast.makeText(requireContext(), "Please allow the permission", Toast.LENGTH_SHORT).show()
            requestPermissions()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        lifecycleScope.launch {
            binding.animationView.resumeAnimation()

            delay(3000)

            requestPermissions()
        }
    }

    private fun startHomePage() {
        if (!VoiceChangerApplication.hasPermissions(requireContext(),
                VoiceChangerApplication.ALL_PERMISSIONS_LIST.toString()
            )) {
            ActivityCompat.requestPermissions(requireActivity(), VoiceChangerApplication.ALL_PERMISSIONS_LIST, VoiceChangerApplication.PERMISSION_TOKEN)
        } else {
            appNavigation.openSplashToHomeScreen()
        }
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(VoiceChangerApplication.ALL_PERMISSIONS_LIST)
    }
}