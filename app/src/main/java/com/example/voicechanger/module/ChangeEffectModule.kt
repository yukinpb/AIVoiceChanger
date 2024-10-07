package com.example.voicechanger.module

import android.content.Context
import android.util.Log
import com.example.voicechanger.model.AudioAttrModel
import com.example.voicechanger.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ChangeEffectModule(
    private val context: Context
) {
    private var modelEffects = ArrayList<AudioAttrModel>()
    private var audioPath: String = ""
    private var indexPLaying: Int? = null
    private var mediaPlayer: BASSMediaPlayer? = null

    fun setAudioPath(audioPath: String) {
        this.audioPath = audioPath
    }

    fun insertEffect(effects: String?) {
        effects?.let {
            val parsedEffects = ParsingJsonObjects.jsonToObjectEffects(it)
            parsedEffects?.let { effects ->
                modelEffects.addAll(effects)
            }
        }
    }

    fun saveEffect(finalFile: File, onSuccess: () -> Unit) {
        saveEffect(modelEffects[indexPLaying!!], finalFile, onSuccess)
    }

    fun createMediaPlayer(onMediaCompleted: () -> Unit) {
        if (audioPath.isNotEmpty()) {
            mediaPlayer = BASSMediaPlayer(audioPath)
            mediaPlayer?.prepare()
            this.mediaPlayer?.setMediaListener(object : MediaListener {
                override fun onMediaErrorListener() {}

                override fun onMediaCompleteListener() {
                    modelEffects[indexPLaying!!].isPlaying = false
                    indexPLaying = null
                    onMediaCompleted()
                }
            })
        } else {
            context.toast("Media file not found!")
        }
    }

    fun getMediaPlayer(): BASSMediaPlayer? {
        return mediaPlayer
    }

    fun applyEffect(i: Int) {
        Log.d(TAG, "audioPath: $audioPath")
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
            modelEffects.isPlaying = false
            mediaPlayer?.pause()
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

            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    if (mediaPlayer.initSolveToMedia()) {
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