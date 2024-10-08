package com.example.voicechanger.navigation

import android.os.Bundle
import com.example.voicechanger.navigationComponent.BaseNavigator

interface AppNavigation : BaseNavigator {
    fun openSplashToHomeScreen(bundle: Bundle? = null)
    fun openSplashToLanguageScreen(bundle: Bundle? = null)
    fun openLanguageToHomeScreen(bundle: Bundle? = null)
    fun openHomeToLanguageScreen(bundle: Bundle? = null)
    fun openHomeToRecordingScreen(bundle: Bundle? = null)
    fun openRecordingToChangeEffectScreen(bundle: Bundle? = null)
    fun openChangeEffectToPlayerScreen(bundle: Bundle? = null)
    fun openRecordingToHomeScreen(bundle: Bundle? = null)
    fun openAudioPlayerToRecordingScreen(bundle: Bundle? = null)
    fun openAudioPlayerToHomeScreen(bundle: Bundle? = null)
    fun openHomeToTextToAudioScreen(bundle: Bundle? = null)
    fun openTextToAudioToChangeEffectScreen(bundle: Bundle? = null)
    fun openHomeToAudioListScreen(bundle: Bundle? = null)
    fun openAudioListToAudioPlayerScreen(bundle: Bundle? = null)
    fun openAudioListToChangeEffectScreen(bundle: Bundle? = null)
    fun openAudioListToEditAudioScreen(bundle: Bundle? = null)
    fun openHomeToAIVoiceMakerScreen(bundle: Bundle? = null)
    fun openAIVoiceMakerToPlayerScreen(bundle: Bundle? = null)
}