package com.example.voicechanger.navigationComponent

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController

abstract class BaseNavigatorImpl : BaseNavigator {

    override var navController: NavController? = null

    override fun bind(navController: NavController) {
        this.navController = navController
    }

    override fun unbind() {
        navController = null
    }

    override fun openScreen(
        @IdRes id: Int,
        bundle: Bundle?
    ) {
        navController?.navigate(id, bundle)
    }

    override fun navigateUp() = navController?.navigateUp()

    override fun currentFragmentId() = navController?.currentDestination?.id

    override fun setStartDestination(@IdRes id: Int) {
        if (navController == null) {
            return
        }
        val navGraph = navController?.graph
        navGraph?.setStartDestination(id)
        if (navGraph != null) {
            navController?.graph = navGraph
        }
    }
}