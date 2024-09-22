package com.example.voicechanger.pref

import android.content.Context
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
    }

    fun getLanguage(): Flow<LanguageModel?> =
        getValue(PreferencesKeys.PREF_PARAM_LANGUAGE).map { languageCode ->
            languageCode?.let { code ->
                LanguageProvider.getLanguages().find { it.locale.toLanguageTag() == code }
            } ?: LanguageProvider.getLanguages().find { it.locale == Locale.US }
        }

    suspend fun setLanguage(language: LanguageModel) {
        putValue(PreferencesKeys.PREF_PARAM_LANGUAGE, language.locale.toLanguageTag())
    }
}