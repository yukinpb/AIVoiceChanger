package com.example.voicechanger.module

import android.content.Context
import android.util.Log
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.example.voicechanger.model.AudioAttrModel
import com.example.voicechanger.utils.toDoubleList
import com.example.voicechanger.utils.toIntList
import org.json.JSONArray
import org.json.JSONObject

class AudioChangerModule(
    private val context: Context
) {
    private val effectsMap = mutableMapOf<Int, AudioAttrModel>()

    init {
        loadEffects()
    }

    private fun loadEffects() {
        val json = context.assets.open("effects.json").bufferedReader().use { it.readText() }
        val effectsArray = JSONArray(json)
        for (i in 0 until effectsArray.length()) {
            val effect = effectsArray.getJSONObject(i)
            val audioEffect = parseAudioEffect(effect)
            effectsMap[audioEffect.id] = audioEffect
        }
    }

    private fun parseAudioEffect(effect: JSONObject): AudioAttrModel {
        return AudioAttrModel(
            id = effect.getInt("id"),
            icon = effect.getString("icon"),
            name = effect.getString("name"),
            asetrate = effect.optString("asetrate", ""),
            atempo = effect.optString("atempo", ""),
            aecho = effect.optString("aecho", ""),
            volume = effect.optString("volume", null),
            areverse = effect.optBoolean("areverse", false)
        )
    }

    fun applyEffect(
        effectId: Int,
        inputFile: String,
        outputFile: String,
        onPrepare: () -> Unit,
        onSuccess: () -> Unit,
        onCancel: () -> Unit,
        onError: (String) -> Unit
    ) : Boolean {
        val effect = effectsMap[effectId] ?: return false
        val filter = buildFilterString(effect)
        if (filter.isEmpty()) {
            return false
        }
        val cmd = arrayOf("-y", "-i", inputFile, "-af", filter, outputFile)
        executeFFMPEG(cmd, onPrepare, onSuccess, onCancel, onError)

        return true
    }

    private fun buildFilterString(effect: AudioAttrModel): String {
        val filters = mutableListOf<String>()
        if (effect.asetrate.isNotEmpty()) filters.add("asetrate=${effect.asetrate}")
        if (effect.atempo.isNotEmpty()) filters.add("atempo=${effect.atempo}")
        if (effect.aecho.isNotEmpty()) filters.add("aecho=${effect.aecho}")
        if (effect.volume != null) filters.add("volume=${effect.volume}")
        if (effect.areverse) filters.add("areverse")
        return filters.joinToString(",")
    }

    private fun executeFFMPEG(
        cmd: Array<String>,
        onPrepare: () -> Unit,
        onSuccess: () -> Unit,
        onCancel: () -> Unit,
        onError: (String) -> Unit
    ){
        onPrepare()
        FFmpeg.execute(cmd)
        val rc = Config.getLastReturnCode()
        val output = Config.getLastCommandOutput()

        when (rc) {
            Config.RETURN_CODE_SUCCESS -> {
                onSuccess()
            }

            Config.RETURN_CODE_CANCEL -> {
                onCancel()
            }

            else -> {
                onError(output)
            }
        }
    }
}