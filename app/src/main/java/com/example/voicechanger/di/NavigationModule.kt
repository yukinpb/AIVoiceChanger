package com.example.voicechanger.di

import com.example.voicechanger.navigation.AppNavigation
import com.example.voicechanger.navigation.AppNavigatorImpl
import com.example.voicechanger.navigationComponent.BaseNavigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class NavigationModule {

    @Binds
    @ActivityScoped
    abstract fun provideBaseNavigation(navigation: AppNavigatorImpl): BaseNavigator

    @Binds
    @ActivityScoped
    abstract fun provideAppNavigation(navigation: AppNavigatorImpl): AppNavigation
}