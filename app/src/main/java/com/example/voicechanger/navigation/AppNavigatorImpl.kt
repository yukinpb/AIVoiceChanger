package com.example.voicechanger.navigation

import android.os.Bundle
import com.example.voicechanger.R
import com.example.voicechanger.navigation.AppNavigation
import com.example.voicechanger.navigationComponent.BaseNavigatorImpl
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class AppNavigatorImpl @Inject constructor() : BaseNavigatorImpl(),
    AppNavigation {

    override fun openSplashToHomeScreen(bundle: Bundle?) {
        openScreen(R.id.action_slashFragment_to_homeFragment, bundle)
    }

    override fun openHomeToRecordingScreen(bundle: Bundle?) {
        openScreen(R.id.action_homeFragment_to_recordingFragment, bundle)
    }

    override fun openRecordingToChangeEffectScreen(bundle: Bundle?) {
        openScreen(R.id.action_recordingFragment_to_changeEffectFragment, bundle)
    }

    override fun openChangeEffectToPlayerScreen(bundle: Bundle?) {
        openScreen(R.id.action_changeEffectFragment_to_audioPlayerFragment, bundle)
    }

    override fun openRecordingToHomeScreen(bundle: Bundle?) {
        openScreen(R.id.action_recordingFragment_to_homeFragment, bundle)
    }

    override fun openAudioPlayerToRecordingScreen(bundle: Bundle?) {
        openScreen(R.id.action_audioPlayerFragment_to_recordingFragment, bundle)
    }

    override fun openAudioPlayerToHomeScreen(bundle: Bundle?) {
        openScreen(R.id.action_audioPlayerFragment_to_homeFragment, bundle)
    }

    override fun openHomeToTextToAudioScreen(bundle: Bundle?) {
        openScreen(R.id.action_homeFragment_to_textToAudioFragment, bundle)
    }

    override fun openTextToAudioToChangeEffectScreen(bundle: Bundle?) {
        openScreen(R.id.action_textToAudioFragment_to_changeEffectFragment, bundle)
    }

    override fun openHomeToAudioListScreen(bundle: Bundle?) {
        openScreen(R.id.action_homeFragment_to_audioListFragment, bundle)
    }

    override fun openAudioListToAudioPlayerScreen(bundle: Bundle?) {
        openScreen(R.id.action_audioListFragment_to_audioPlayerFragment, bundle)
    }

    override fun openAudioListToChangeEffectScreen(bundle: Bundle?) {
        openScreen(R.id.action_audioListFragment_to_changeEffectFragment, bundle)
    }
}