package com.example.voicechanger.viewModel

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arthenica.mobileffmpeg.FFmpeg
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

    private val outputDir =
        "${context.getVoiceEffectDirPath()}/ai_tts_${System.currentTimeMillis()}.wav"

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    fun applyEffect(id: Int) {
        globalPerson = persons.find { it.id == id }
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
                Log.d("hainv", "fetchVoice() called : $voiceUrl")
                downloadAndSaveVoice(voiceUrl)
            } catch (error: Exception) {
                hideLoading()
                _isSuccess.value = false
            }
        }
    }

    private suspend fun postVoice(tokenModel: TokenModel): String {
        showLoading()
        val response = apiInterface.postVoice(tokenModel)
        val token = response.inferenceJobToken ?: throw Exception("Invalid response token")

        while (true) {
            delay(500)
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
                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val uri = Uri.parse(voiceUrl)
                val request = DownloadManager.Request(uri)
                    .setTitle("Downloading Voice")
                    .setDescription("Downloading voice file using DownloadManager")
                    .setDestinationUri(Uri.fromFile(File(outputDir)))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                downloadManager.enqueue(request)
                hideLoading()
                _isSuccess.postValue(true)
            } catch (e: Exception) {
                hideLoading()
                _isSuccess.postValue(false)
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