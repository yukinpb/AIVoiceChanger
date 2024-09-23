package com.example.voicechanger.pref

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.voicechanger.model.LanguageModel
import com.example.voicechanger.utils.LanguageProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext context: Context
) : BasePreferencesImpl(context) {

    private object PreferencesKeys {
        val PREF_PARAM_LANGUAGE = stringPreferencesKey("PREF_PARAM_LANGUAGE")
        val PREF_PARAM_FIRST_TIME = booleanPreferencesKey("PREF_PARAM_FIRST_TIME")
    }

    fun getLanguage(): Flow<String> = getValue(PreferencesKeys.PREF_PARAM_LANGUAGE)
        .map { it ?: "en" }

    suspend fun setLanguage(language: LanguageModel) {
        putValue(PreferencesKeys.PREF_PARAM_LANGUAGE, language.locale)
    }

    fun isFirstTime(): Flow<Boolean> = getValue(PreferencesKeys.PREF_PARAM_FIRST_TIME)
        .map { it ?: true }

    suspend fun setFirstTime(firstTime: Boolean) {
        putValue(PreferencesKeys.PREF_PARAM_FIRST_TIME, firstTime)
    }
}