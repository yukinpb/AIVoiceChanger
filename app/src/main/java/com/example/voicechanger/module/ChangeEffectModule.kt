package com.example.voicechanger.module

import android.content.Context
import android.util.Log
import com.example.voicechanger.model.AudioAttrModel
import com.example.voicechanger.utils.toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.un4seen.bass.BASS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class ChangeEffectModule(
    private val context: Context
) {
    private var isInit = false
    private var modelEffects = mutableListOf<AudioAttrModel>()
    private var audioPath: String = ""
    private var indexPLaying: Int? = null
    private var mediaPlayer: BASSMediaPlayer? = null

    init {
        onInitAudioDevice()
    }

    fun setAudioPath(audioPath: String) {
        this.audioPath = audioPath
    }

    private fun parseAudioAttrModels(context: Context): List<AudioAttrModel> {
        val gson = Gson()
        val inputStream = context.assets.open("effects.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<AudioAttrModel>>() {}.type
        return gson.fromJson(reader, type)
    }

    fun saveEffect(finalFile: File, onSuccess: () -> Unit) {
        saveEffect(modelEffects[indexPLaying!!], finalFile, onSuccess)
    }

    fun prepare() {
        if (audioPath.isNotEmpty()) {
            mediaPlayer = BASSMediaPlayer(audioPath)
            mediaPlayer?.prepare()
        } else {
            context.toast("Media file not found!")
        }
    }

    fun setMediaListener(mediaListener: MediaListener) {
        mediaPlayer?.setMediaListener(mediaListener)
    }

    fun getMediaPlayer(): BASSMediaPlayer? {
        return mediaPlayer
    }

    fun applyEffect(i: Int) {
        if (audioPath.isNotEmpty()) {
            val file = File(audioPath)
            if (!file.exists() || !file.isFile) {
                context.toast("File not found!")
            }
        }
        try {
            indexPLaying = i
            applyEffect(modelEffects[i])
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyEffect(modelEffects: AudioAttrModel) {
        if (modelEffects.isPlaying) {
            return
        }
        onStateReset()
        modelEffects.isPlaying = true
        mediaPlayer?.let {
            it.prepare()
            it.setReverse(modelEffects.isReverse)
            it.setPitch(modelEffects.pitch)
            it.setCompressor(modelEffects.compressor)
            it.setRate(modelEffects.rate)
            it.setEQ1Audio(modelEffects.echo1)
            it.setEQ2Audio(modelEffects.eq2)
            it.setEQ3Audio(modelEffects.eq3)
            it.setPhaser(modelEffects.phaser)
            it.setAutoWah(modelEffects.autoWah)
            it.setReverb(modelEffects.reverb)
            it.setEffectEcho4(modelEffects.echo4)
            it.setEcho(modelEffects.echo)
            it.setFilterQuad(modelEffects.filter)
            it.setFlanger(modelEffects.flanger)
            it.setChorus(modelEffects.chorus)
            it.setAmplify(modelEffects.amplify)
            it.setDistort(modelEffects.distort)
            it.setRotate(modelEffects.rotate)
            it.start()
        }
    }

    private fun saveEffect(modelEffects: AudioAttrModel, finalFile: File, onSuccess: () -> Unit) {
        if (mediaPlayer != null) {
            val mediaPlayer = BASSMediaPlayer(audioPath)

            runBlocking  {
                withContext(Dispatchers.IO) {
                    if (mediaPlayer.initSolveToMedia()) {
                        Log.d("hainv", "saveEffect: 1")
                        mediaPlayer.setReverse(modelEffects.isReverse)
                        mediaPlayer.setPitch(modelEffects.pitch)
                        mediaPlayer.setCompressor(modelEffects.compressor)
                        mediaPlayer.setRate(modelEffects.rate)
                        mediaPlayer.setEQ1Audio(modelEffects.echo1)
                        mediaPlayer.setEQ2Audio(modelEffects.eq2)
                        mediaPlayer.setEQ3Audio(modelEffects.eq3)
                        mediaPlayer.setPhaser(modelEffects.phaser)
                        mediaPlayer.setAutoWah(modelEffects.autoWah)
                        mediaPlayer.setReverb(modelEffects.reverb)
                        mediaPlayer.setEffectEcho4(modelEffects.echo4)
                        mediaPlayer.setEcho(modelEffects.echo)
                        mediaPlayer.setFilterQuad(modelEffects.filter)
                        mediaPlayer.setFlanger(modelEffects.flanger)
                        mediaPlayer.setChorus(modelEffects.chorus)
                        mediaPlayer.setAmplify(modelEffects.amplify)
                        mediaPlayer.setDistort(modelEffects.distort)
                        mediaPlayer.setRotate(modelEffects.rotate)
                        mediaPlayer.saveFile(finalFile.absolutePath)
                        mediaPlayer.release()
                    }
                }
                onSuccess()
            }
        }
    }

    private fun onInitAudioDevice() {
        if (isInit) {
            return
        }
        isInit = true
        modelEffects.addAll(parseAudioAttrModels(context))
        if (!BASS.BASS_Init(-1, 44100, 0)) {
            Exception("VoiceChangerModule Can't initialize device").printStackTrace()
            isInit = false
            return
        }
        val str = context.applicationInfo.nativeLibraryDir
        try {
            BASS.BASS_PluginLoad("$str/libbass_fx.so", 0)
            BASS.BASS_PluginLoad("$str/libbassmix.so", 0)
            BASS.BASS_PluginLoad("$str/libbassenc.so", 0)
            BASS.BASS_PluginLoad("$str/libbasswv.so", 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onStateReset() {
        modelEffects.forEach {
            if (it.isPlaying) {
                it.isPlaying = false
            }
        }
    }

    companion object {
        const val TAG = "VoiceChangerModule"
    }
}