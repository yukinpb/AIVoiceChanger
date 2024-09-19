package com.example.voicechanger.pref

import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface RxPreferences : BasePreferences {

    fun getToken(): Flow<String?>

    suspend fun setUserToken(userToken: String)

    fun getLanguage(): Flow<String?>

    suspend fun setLanguage(language: String)

    fun logout()

}