package com.example.voicechanger.viewModel

import android.app.DownloadManager
import android.content.Context
import android.media.MediaMetadataRetriever
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    private val _audioModel = MutableLiveData<AudioModel>()
    val audioModel: LiveData<AudioModel> get() = _audioModel

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
                val (duration, size) = getAudioInfo(voiceUrl)

                val audioModel = AudioModel(
                    path = voiceUrl,
                    fileName = "GeneratedVoice_${System.currentTimeMillis()}.wav",
                    duration = duration.toString(),
                    dateCreate = System.currentTimeMillis(),
                    size = size.toString()
                )

                _audioModel.value = audioModel
            } catch (error: Exception) {
                hideLoading()
                _isSuccess.value = false
            } finally {
                hideLoading()
            }
        }
    }

    private suspend fun getAudioInfo(audioUrl: String): Pair<Long, Long> {
        return withContext(Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(audioUrl, HashMap())
                val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
                val size = URL(audioUrl).openConnection().contentLengthLong
                retriever.release()
                Pair(duration, size)
            } catch (e: Exception) {
                e.printStackTrace()
                Pair(0L, 0L) // Return default values in case of an error
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
}