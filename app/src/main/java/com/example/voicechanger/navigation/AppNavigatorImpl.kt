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
}