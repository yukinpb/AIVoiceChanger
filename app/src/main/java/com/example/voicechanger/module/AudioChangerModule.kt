package com.example.voicechanger.module

import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.example.voicechanger.model.AudioAttrModel

class AudioChangerModule {
    private val effects : List<AudioAttrModel> = AudioAttrModel.audioAttr

    fun applyEffect(
        effectId: Int,
        inputFile: String,
        outputFile: String,
        onPrepare: () -> Unit,
        onSuccess: () -> Unit,
        onCancel: () -> Unit,
        onError: (String) -> Unit
    ) : Boolean {
        val effect = effects[effectId] ?: return false
        val filter = effect.effect
        if (filter.isEmpty()) {
            return false
        }
        val cmd = arrayOf("-y", "-i", inputFile, "-af", filter, outputFile)
        executeFFMPEG(cmd, onPrepare, onSuccess, onCancel, onError)

        return true
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