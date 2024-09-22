package com.example.voicechanger.viewModel

import android.app.Application
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.voicechanger.base.BaseViewModel
import com.example.voicechanger.model.LanguageModel
import com.example.voicechanger.pref.AppPreferences
import com.example.voicechanger.utils.getVoiceRecordDirPath
import com.example.voicechanger.utils.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class TextToAudioViewModel @Inject constructor(
    private val application: Application,
    private val appPreferences: AppPreferences
) : BaseViewModel(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech = TextToSpeech(application, this)

    private val _language = MutableLiveData<LanguageModel?>()
    val language: LiveData<LanguageModel?> get() = _language

    var attempts = 0
    val maxAttempts = 3

    init {
        viewModelScope.launch {
            appPreferences.getLanguage().collect { language ->
                language?.let {
                    setLanguage(it)
                    _language.postValue(it)
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = _language.value?.locale
        }
    }

    fun saveAudio(text: String, onSaved: (String) -> Unit) {
        val outputDir = application.getVoiceRecordDirPath()
        val filePath = "$outputDir/tts_${System.currentTimeMillis()}.wav"
        val file = File(filePath)
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, file.name)
        }

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                Log.d("TTS", "Start save file: $utteranceId")
                showLoading()
            }

            override fun onDone(utteranceId: String) {
                Log.d("TTS", "Finish save file: $utteranceId")
                onSaved(filePath)
                hideLoading()
            }

            override fun onError(utteranceId: String) {
                Log.e("TTS", "Error save file: $utteranceId")
                hideLoading()
                if (attempts < maxAttempts) {
                    Log.d("TTS", "Retrying save file: attempt $attempts")
                    tts.synthesizeToFile(text, params, file, file.name)
                } else {
                    Log.e("TTS", "Failed to save file after $maxAttempts attempts")
                }
            }

            override fun onError(utteranceId: String, errorCode: Int) {
                Log.e("TTS", "Error with code: $errorCode for utteranceId: $utteranceId")
                hideLoading()
                if (attempts < maxAttempts) {
                    Log.d("TTS", "Retrying save file: attempt $attempts")
                    tts.synthesizeToFile(text, params, file, file.name)
                } else {
                    Log.e("TTS", "Failed to save file after $maxAttempts attempts")
                }
            }
        })

        tts.synthesizeToFile(text, params, file, file.name)
    }

    fun setLanguage(language: LanguageModel) {
        tts.language = language.locale
        viewModelScope.launch {
            updateLanguage(language)
        }
    }

    private suspend fun updateLanguage(language: LanguageModel) {
        appPreferences.setLanguage(language)
    }

    override fun onCleared() {
        if (tts.isSpeaking) {
            tts.stop()
        }
        tts.shutdown()
        super.onCleared()
    }
}