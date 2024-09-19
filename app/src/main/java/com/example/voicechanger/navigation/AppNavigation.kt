package com.example.voicechanger.navigation

import android.os.Bundle
import com.example.voicechanger.navigationComponent.BaseNavigator

interface AppNavigation : BaseNavigator {
    fun openSplashToHomeScreen(bundle: Bundle? = null)
}