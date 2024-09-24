package com.example.voicechanger.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.voicechanger.base.BaseViewModel
import com.example.voicechanger.model.AudioEffectModel
import com.example.voicechanger.model.AudioModel
import com.example.voicechanger.model.TokenModel
import com.example.voicechanger.network.ApiInterface
import com.example.voicechanger.repository.AudioEffectRepository
import com.example.voicechanger.utils.getDuration
import com.example.voicechanger.utils.getSize
import com.example.voicechanger.utils.getVoiceEffectDirPath
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class AIVoiceMakerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiInterface: ApiInterface,
    private val repository: AudioEffectRepository
) : BaseViewModel() {

    private var persons: List<AudioEffectModel> = repository.getAIEffect()
    private var globalPerson: AudioEffectModel? = null

    val outputDir = "${context.getVoiceEffectDirPath()}/ai_tts_${System.currentTimeMillis()}.wav"

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    fun applyEffect(index: Int) {
        globalPerson = persons[index]
    }

    fun fetchVoice(controllerText: String) {
        viewModelScope.launch {
            if (_isSuccess.value == true) {
                _isSuccess.value = false
            }

            val tokenModel = TokenModel(
                ttsModelToken = globalPerson?.token,
                uuidIdempotencyToken = java.util.UUID.randomUUID().toString(),
                inferenceText = controllerText
            )

            try {
                val voiceUrl = postVoice(tokenModel)
                downloadAndSaveVoice(voiceUrl)
            } catch (error: Exception) {
                println("Error: $error")
            } finally {
                _isSuccess.value = false
            }
        }
    }

    private suspend fun postVoice(tokenModel: TokenModel): String {
        showLoading()
        val response = apiInterface.postVoice(tokenModel)
        val token = response.inferenceJobToken ?: throw Exception("Invalid response token")

        while (true) {
            delay(200)
            val voiceResponse = apiInterface.getVoice(token)
            if (voiceResponse.state.status == "complete_success") {
                val path = voiceResponse.state.maybePublicBucketWavAudioPath
                    ?: throw Exception("Invalid voice path")
                return "https://storage.googleapis.com/vocodes-public$path"
            }
        }
    }

    private fun downloadAndSaveVoice(voiceUrl: String) {
        viewModelScope.launch {
            try {
                val url = URL(voiceUrl)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.doOutput = true
                connection.connect()

                val file = File(outputDir)
                val inputStream: InputStream = connection.inputStream
                val outputStream = FileOutputStream(file)

                val buffer = ByteArray(1024)
                var len: Int
                while (inputStream.read(buffer).also { len = it } != -1) {
                    outputStream.write(buffer, 0, len)
                }

                outputStream.close()
                inputStream.close()
                _isSuccess.postValue(true)
                hideLoading()
            } catch (e: Exception) {
                e.printStackTrace()
                _isSuccess.postValue(false)
                hideLoading()
            }
        }
    }

    fun getAudioSaved(): AudioModel {
        val file = File(outputDir)
        val duration = file.getDuration()
        val size = file.getSize()
        return AudioModel(outputDir, file.name, duration, file.lastModified(), size)
    }
}