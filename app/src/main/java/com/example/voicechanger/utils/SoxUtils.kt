package com.example.voicechanger.utils

object SoxUtils {
    init {
        System.loadLibrary("xessox-lib")
    }

    external fun soxAudio(inputPath: String, outputPath: String)

    private external fun initSoxAudio(
        bufferSize: Int, sampleRate: Double, channels: Int, reverberance: Int, damping: Int,
        roomScale: Int, stereoDepth: Int, preDelay: Int, wetGain: Int
    ): Boolean

    private external fun process(audioSample: ShortArray, size: Int): ShortArray

    external fun destory()

    fun initReverbEffect(
        bufferSize: Int, sampleRate: Double, channels: Int, reverberance: Int, damping: Int,
        roomScale: Int, stereoDepth: Int, preDelay: Int, wetGain: Int
    ): Boolean {
        return initSoxAudio(bufferSize, sampleRate, channels, reverberance, damping, roomScale, stereoDepth, preDelay, wetGain)
    }

    fun doReverProcess(audioSample: ShortArray, size: Int): ShortArray {
        return process(audioSample, size)
    }

    fun initEqualizerEffect(bufferSize: Int, sampleRate: Double, channels: Int, params: Array<FloatArray>): Boolean {
        return _initEqualizerEffect(bufferSize, sampleRate, channels, params)
    }

    fun doEqualProcess(audioSample: ShortArray, size: Int): ShortArray {
        return _equalProcess(audioSample, size)
    }

    external fun destoryEqual()

    private external fun _equalProcess(audioSample: ShortArray, size: Int): ShortArray

    external fun _initEqualizerEffect(bufferSize: Int, sampleRate: Double, channels: Int, params: Array<FloatArray>): Boolean

    external fun _initEqualizerChain(params: Array<FloatArray>): Boolean

    external fun destoryEqualChain()

    external fun test()
}