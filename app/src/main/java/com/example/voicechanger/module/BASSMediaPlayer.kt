package com.example.voicechanger.module

import android.util.Log
import com.example.voicechanger.utils.Constants
import com.un4seen.bass.BASS
import com.un4seen.bass.BASS.BASS_ATTRIB_MUSIC_SPEED
import com.un4seen.bass.BASS.BASS_CTYPE_STREAM_MF
import com.un4seen.bass.BASS.BASS_FX_DX8_DISTORTION
import com.un4seen.bass.BASS.BASS_FX_DX8_ECHO
import com.un4seen.bass.BASS.BASS_FX_DX8_REVERB
import com.un4seen.bass.BASS.BASS_SAMPLE_OVER_VOL
import com.un4seen.bass.BASS.BASS_TAG_MUSIC_MESSAGE
import com.un4seen.bass.BASS_FX
import com.un4seen.bass.BASS_FX.BASS_BFX_BQF_LOWSHELF
import com.un4seen.bass.BASS_FX.BASS_BFX_BQF_NOTCH
import com.un4seen.bass.BASS_FX.BASS_FX_BFX_BQF
import com.un4seen.bass.BASS_FX.BASS_FX_BFX_CHORUS
import com.un4seen.bass.BASS_FX.BASS_FX_BFX_COMPRESSOR2
import com.un4seen.bass.BASS_FX.BASS_FX_BFX_ECHO4
import com.un4seen.bass.BASS_FX.BASS_FX_BFX_ROTATE
import java.nio.ByteBuffer
import java.util.Locale

class BASSMediaPlayer(
    private val mediaPath: String
) {
    private var amplifyFx = 0
    private var autoEffectFx = 0
    private var bigQuedEffects = 0
    private var chorusEffectFx = 0
    private var compressorEffects = 0
    private var effectDistort = 0
    private var effectEQ2 = 0
    private var effectEQ3 = 0
    private var effectEcho = 0
    private var effectFlanger = 0
    private var effectPhaser = 0
    private var effectReverb = 0
    private var effectRotate = 0
    private var effectEQ1 = 0
    private var effectEQ4 = 0
    private var mediaListener: MediaListener? = null
    private var isReverse = false

    private var currentPosition = 0
    private var duration = 0
    var isPlaying = false
    private var channelPlay = 0

    fun prepare(): Boolean {
        if (mediaPath.isEmpty()) {
            return false
        }
        if (mediaPath.lowercase(Locale.getDefault()).endsWith(Constants.MediaConstants.mp3Type) ||
            mediaPath.lowercase(Locale.getDefault()).endsWith(Constants.MediaConstants.wavType) ||
            mediaPath.lowercase(Locale.getDefault()).endsWith(Constants.MediaConstants.oggType) ||
            mediaPath.lowercase(Locale.getDefault()).endsWith(Constants.MediaConstants.flacType) ||
            mediaPath.lowercase(Locale.getDefault()).endsWith(Constants.MediaConstants.aacType) ||
            mediaPath.lowercase(Locale.getDefault()).endsWith(Constants.MediaConstants.midType) ||
            mediaPath.lowercase(Locale.getDefault()).endsWith(Constants.MediaConstants.wmaType)
        ) {
            init()
            return true
        }
        return false
    }

    fun setMediaListener(mediaListener: MediaListener) {
        this.mediaListener = mediaListener
    }

    fun setVolume(volume: Float) {
        if (channelPlay != 0) {
            BASS.BASS_ChannelSetAttribute(channelPlay, BASS.BASS_ATTRIB_VOL, volume)
        }
    }

    fun setSpeed(speed: Float) {
        if (channelPlay != 0) {
            BASS.BASS_ChannelSetAttribute(channelPlay, BASS_ATTRIB_MUSIC_SPEED, speed)
        }
    }

    private fun init() {
        release()
        val str = this.mediaPath
        if (str != "") {
            this.channelPlay = BASS.BASS_StreamCreateFile(this.mediaPath, 0L, 0L, 2097152)
        }
        var i = this.channelPlay
        if (i != 0) {
            this.channelPlay = BASS_FX.BASS_FX_ReverseCreate(i, 2.0f, 2162688)
            i = this.channelPlay
            if (i != 0) {
                BASS.BASS_ChannelGetInfo(i, BASS.BASS_CHANNELINFO())
                val tempoCreate = BASS_FX.BASS_FX_TempoCreate(this.channelPlay, 65536)
                this.channelPlay = tempoCreate
                if (tempoCreate == 0) {
                    Log.d(TAG, "BASS_Error=" + BASS.BASS_ErrorGetCode())
                    BASS.BASS_StreamFree(this.channelPlay)
                    return
                }
                return
            }
            Log.d(TAG, "BASS_Error=" + BASS.BASS_ErrorGetCode())
            BASS.BASS_StreamFree(this.channelPlay)
            return
        }
        Log.d(TAG, "BASS_Error=" + BASS.BASS_ErrorGetCode())
        BASS.BASS_StreamFree(this.channelPlay)
    }

    fun initSolveToMedia(): Boolean {
        BASS.BASS_StreamFree(this.channelPlay)
        val streamCreateFile = BASS.BASS_StreamCreateFile(mediaPath, 0L, 0L, 2097152)
        this.channelPlay = streamCreateFile
        if (streamCreateFile != 0) {
            this.channelPlay = BASS_FX.BASS_FX_ReverseCreate(streamCreateFile, 2.0f, 2097152)
            val i = this.channelPlay
            if (i != 0) {
                val tempoCreate = BASS_FX.BASS_FX_TempoCreate(i, 2097152)
                this.channelPlay = tempoCreate
                if (tempoCreate != 0) {
                    return true
                }
                Exception("DBMediaPlayer Couldnt create a resampled stream!").printStackTrace()
                BASS.BASS_StreamFree(this.channelPlay)
                return false
            }
            Exception("DBMediaPlayer Couldnt create a resampled stream!").printStackTrace()
            BASS.BASS_StreamFree(this.channelPlay)
        }
        return false
    }

    fun start() {
        isPlaying = true
        if (channelPlay != 0) {
            Log.d(TAG, "start: $channelPlay")
            BASS.BASS_ChannelPlay(channelPlay, false)
        }
    }

    fun pause() {
        if (!this.isPlaying) {
            return
        }
        val i = this.channelPlay
        if (i != 0) {
            BASS.BASS_ChannelPause(i)
        }
    }

    fun getDuration(): Int {
        if (this.channelPlay != 0) {
            this.duration = getChannelLength()
        }
        return this.duration
    }

    fun getCurrentPosition(): Int {
        return this.currentPosition
    }

    fun seekTo(i: Int) {
        if (!this.isPlaying) {
            Exception("MediaPlayer is not playing").printStackTrace()
        } else {
            this.currentPosition = i
            seekToChannel(i)
        }
    }

    fun saveFile(str: String?) {
        var channelGetData: Int
        if (str.isNullOrEmpty() || channelPlay == 0)  {
            return
        }
        try {
            val allocateDirect = ByteBuffer.allocateDirect(20000)
            do {
                channelGetData = BASS.BASS_ChannelGetData(channelPlay, allocateDirect, allocateDirect.capacity())
                if (channelGetData == -1) {
                    return
                }
            } while (channelGetData != 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        var i = this.effectReverb
        if (i != 0) {
            BASS.BASS_ChannelRemoveFX(this.channelPlay, i)
            this.effectReverb = 0
        }
        i = this.effectFlanger
        if (i != 0) {
            BASS.BASS_ChannelRemoveFX(this.channelPlay, i)
            this.effectReverb = 0
        }
        i = this.effectEcho
        if (i != 0) {
            BASS.BASS_ChannelRemoveFX(this.channelPlay, i)
            this.effectEcho = 0
        }
        i = this.bigQuedEffects
        if (i != 0) {
            BASS.BASS_ChannelRemoveFX(this.channelPlay, i)
            this.bigQuedEffects = 0
        }
        i = this.amplifyFx
        if (i != 0) {
            BASS.BASS_ChannelRemoveFX(this.channelPlay, i)
            this.amplifyFx = 0
        }
        i = this.effectDistort
        if (i != 0) {
            BASS.BASS_ChannelRemoveFX(this.channelPlay, i)
            this.effectDistort = 0
        }
        i = this.chorusEffectFx
        if (i != 0) {
            BASS.BASS_ChannelRemoveFX(this.channelPlay, i)
            this.chorusEffectFx = 0
        }
        i = this.effectEQ4
        if (i != 0) {
            BASS.BASS_ChannelRemoveFX(this.channelPlay, i)
            this.effectEQ4 = 0
        }
        i = this.effectEQ1
        if (i != 0) {
            BASS.BASS_ChannelRemoveFX(this.channelPlay, i)
            this.effectEQ1 = 0
        }
        i = this.effectEQ2
        if (i != 0) {
            BASS.BASS_ChannelRemoveFX(this.channelPlay, i)
            this.effectEQ2 = 0
        }
        i = this.effectEQ3
        if (i != 0) {
            BASS.BASS_ChannelRemoveFX(this.channelPlay, i)
            this.effectEQ3 = 0
        }
        i = this.effectRotate
        if (i != 0) {
            BASS.BASS_ChannelRemoveFX(this.channelPlay, i)
            this.effectRotate = 0
        }
        i = this.effectPhaser
        if (i != 0) {
            BASS.BASS_ChannelRemoveFX(this.channelPlay, i)
            this.effectPhaser = 0
        }
        i = this.autoEffectFx
        if (i != 0) {
            BASS.BASS_ChannelRemoveFX(this.channelPlay, i)
            this.autoEffectFx = 0
        }
        i = this.compressorEffects
        if (i != 0) {
            BASS.BASS_ChannelRemoveFX(this.channelPlay, i)
            this.compressorEffects = 0
        }
        this.isPlaying = false
        BASS.BASS_StreamFree(this.channelPlay)
        this.channelPlay = 0
    }

    fun setPitch(pitch: Int) {
        if (channelPlay != 0) {
            BASS.BASS_ChannelSetAttribute(channelPlay, BASS_TAG_MUSIC_MESSAGE, pitch.toFloat())
        }
    }

    fun setRate(rate: Int) {
        if (channelPlay != 0) {
            BASS.BASS_ChannelSetAttribute(channelPlay, BASS_SAMPLE_OVER_VOL, rate.toFloat())
        }
    }

    fun setReverb(params: List<Float>?) {
        if (channelPlay != 0) {
            if (params != null) {
                if (effectReverb == 0) {
                    effectReverb = BASS.BASS_ChannelSetFX(channelPlay, BASS_FX_DX8_REVERB, 0)
                }
                if (effectReverb != 0) {
                    val reverb = BASS.BASS_DX8_REVERB()
                    BASS.BASS_FXGetParameters(effectReverb, reverb)
                    reverb.fReverbMix = params[0]
                    reverb.fReverbTime = params[1]
                    reverb.fHighFreqRTRatio = params[2]
                    BASS.BASS_FXSetParameters(effectReverb, reverb)
                }
            } else {
                if (effectReverb != 0) {
                    BASS.BASS_ChannelRemoveFX(channelPlay, effectReverb)
                    effectReverb = 0
                }
            }
        }
    }

    fun setEcho(params: List<Float>?) {
        if (channelPlay != 0) {
            if (params != null) {
                if (effectEcho == 0) {
                    effectEcho = BASS.BASS_ChannelSetFX(channelPlay, BASS_FX_DX8_ECHO, 0)
                }
                if (effectEcho != 0) {
                    val echo = BASS.BASS_DX8_ECHO()
                    BASS.BASS_FXGetParameters(effectEcho, echo)
                    echo.fLeftDelay = params[0]
                    echo.fRightDelay = params[1]
                    echo.fFeedback = params[2]
                    if (params.size == 4) {
                        echo.fWetDryMix = params[3]
                    }
                    BASS.BASS_FXSetParameters(effectEcho, echo)
                }
            } else {
                if (effectEcho != 0) {
                    BASS.BASS_ChannelRemoveFX(channelPlay, effectEcho)
                    effectEcho = 0
                }
            }
        }
    }

    fun setAmplify(gain: Float) {
        if (channelPlay != 0) {
            if (gain != 0.0f) {
                if (amplifyFx == 0) {
                    amplifyFx = BASS.BASS_ChannelSetFX(channelPlay, BASS_CTYPE_STREAM_MF, 0)
                }
                if (amplifyFx != 0) {
                    val damp = BASS_FX.BASS_BFX_DAMP()
                    BASS.BASS_FXGetParameters(amplifyFx, damp)
                    damp.fGain = gain
                    BASS.BASS_FXSetParameters(amplifyFx, damp)
                }
            } else {
                if (amplifyFx != 0) {
                    BASS.BASS_ChannelRemoveFX(channelPlay, amplifyFx)
                    amplifyFx = 0
                }
            }
        }
    }

    fun setDistort(params: List<Float>?) {
        if (channelPlay != 0) {
            if (params != null) {
                if (effectDistort == 0) {
                    effectDistort = BASS.BASS_ChannelSetFX(channelPlay, BASS_FX_DX8_DISTORTION, 0)
                }
                if (effectDistort != 0) {
                    val distortion = BASS.BASS_DX8_DISTORTION()
                    BASS.BASS_FXGetParameters(effectDistort, distortion)
                    distortion.fEdge = params[0]
                    distortion.fGain = params[1]
                    distortion.fPostEQBandwidth = params[2]
                    distortion.fPostEQCenterFrequency = params[3]
                    distortion.fPreLowpassCutoff = params[4]
                    BASS.BASS_FXSetParameters(effectDistort, distortion)
                }
            } else {
                if (effectDistort != 0) {
                    BASS.BASS_ChannelRemoveFX(channelPlay, effectDistort)
                    effectDistort = 0
                }
            }
        }
    }

    fun setChorus(params: List<Float>?) {
        if (channelPlay != 0) {
            if (params != null) {
                if (chorusEffectFx == 0) {
                    chorusEffectFx = BASS.BASS_ChannelSetFX(channelPlay, BASS_FX_BFX_CHORUS, 0)
                }
                if (chorusEffectFx != 0) {
                    val chorus = BASS_FX.BASS_BFX_CHORUS()
                    BASS.BASS_FXGetParameters(chorusEffectFx, chorus)
                    chorus.fDryMix = params[0]
                    chorus.fWetMix = params[1]
                    chorus.fFeedback = params[2]
                    chorus.fMinSweep = params[3]
                    chorus.fMaxSweep = params[4]
                    chorus.fRate = params[5]
                    BASS.BASS_FXSetParameters(chorusEffectFx, chorus)
                }
            } else {
                if (chorusEffectFx != 0) {
                    BASS.BASS_ChannelRemoveFX(channelPlay, chorusEffectFx)
                    chorusEffectFx = 0
                }
            }
        }
    }

    fun setFilterQuad(params: List<Float>?) {
        if (channelPlay != 0) {
            if (params != null) {
                if (bigQuedEffects == 0) {
                    bigQuedEffects = BASS.BASS_ChannelSetFX(channelPlay, BASS_FX_BFX_BQF, 0)
                }
                if (bigQuedEffects != 0) {
                    val bqf = BASS_FX.BASS_BFX_BQF()
                    BASS.BASS_FXGetParameters(bigQuedEffects, bqf)
                    bqf.lFilter = params[0].toInt()
                    bqf.fCenter = params[1]
                    bqf.fBandwidth = params[2]
                    BASS.BASS_FXSetParameters(bigQuedEffects, bqf)
                }
            } else {
                if (bigQuedEffects != 0) {
                    BASS.BASS_ChannelRemoveFX(channelPlay, bigQuedEffects)
                    bigQuedEffects = 0
                }
            }
        }
    }

    fun setEffectEcho4(params: List<Float>?) {
        if (channelPlay != 0) {
            if (params != null) {
                if (effectEQ4 == 0) {
                    effectEQ4 = BASS.BASS_ChannelSetFX(channelPlay, BASS_FX_BFX_ECHO4, 0)
                }
                if (effectEQ4 != 0) {
                    val echo4 = BASS_FX.BASS_BFX_ECHO4()
                    echo4.fDryMix = params[0]
                    echo4.fWetMix = params[1]
                    echo4.fFeedback = params[2]
                    echo4.fDelay = params[3]
                    echo4.bStereo = false
                    BASS.BASS_FXSetParameters(effectEQ4, echo4)
                }
            } else {
                if (effectEQ4 != 0) {
                    BASS.BASS_ChannelRemoveFX(channelPlay, effectEQ4)
                    effectEQ4 = 0
                }
            }
        }
    }

    fun setEQ1Audio(params: List<Float>?) {
        if (channelPlay != 0) {
            if (params != null) {
                if (effectEQ1 == 0) {
                    effectEQ1 = BASS.BASS_ChannelSetFX(channelPlay, BASS_BFX_BQF_LOWSHELF, 0)
                }
                if (effectEQ1 != 0) {
                    val parameq = BASS.BASS_DX8_PARAMEQ()
                    BASS.BASS_FXGetParameters(effectEQ1, parameq)
                    parameq.fCenter = params[0]
                    parameq.fBandwidth = params[1]
                    parameq.fGain = params[2]
                    BASS.BASS_FXSetParameters(effectEQ1, parameq)
                }
            } else {
                if (effectEQ1 != 0) {
                    BASS.BASS_ChannelRemoveFX(channelPlay, effectEQ1)
                    effectEQ1 = 0
                }
            }
        }
    }

    fun setEQ2Audio(params: List<Float>?) {
        if (channelPlay != 0) {
            if (params != null) {
                if (effectEQ2 == 0) {
                    effectEQ2 = BASS.BASS_ChannelSetFX(channelPlay, BASS_BFX_BQF_LOWSHELF, 0)
                }
                if (effectEQ2 != 0) {
                    val parameq = BASS.BASS_DX8_PARAMEQ()
                    BASS.BASS_FXGetParameters(effectEQ2, parameq)
                    parameq.fCenter = params[0]
                    parameq.fBandwidth = params[1]
                    parameq.fGain = params[2]
                    BASS.BASS_FXSetParameters(effectEQ2, parameq)
                }
            } else {
                if (effectEQ2 != 0) {
                    BASS.BASS_ChannelRemoveFX(channelPlay, effectEQ2)
                    effectEQ2 = 0
                }
            }
        }
    }

    fun setEQ3Audio(params: List<Float>?) {
        if (channelPlay != 0) {
            if (params != null) {
                if (effectEQ3 == 0) {
                    effectEQ3 = BASS.BASS_ChannelSetFX(channelPlay, BASS_BFX_BQF_LOWSHELF, 0)
                }
                if (effectEQ3 != 0) {
                    val parameq = BASS.BASS_DX8_PARAMEQ()
                    BASS.BASS_FXGetParameters(effectEQ3, parameq)
                    parameq.fCenter = params[0]
                    parameq.fBandwidth = params[1]
                    parameq.fGain = params[2]
                    BASS.BASS_FXSetParameters(effectEQ3, parameq)
                }
            } else {
                if (effectEQ3 != 0) {
                    BASS.BASS_ChannelRemoveFX(channelPlay, effectEQ3)
                    effectEQ3 = 0
                }
            }
        }
    }

    fun setRotate(rate: Float) {
        if (channelPlay != 0) {
            if (rate != 0.0f) {
                if (effectRotate == 0) {
                    effectRotate = BASS.BASS_ChannelSetFX(channelPlay, BASS_FX_BFX_ROTATE, 0)
                }
                if (effectRotate != 0) {
                    val rotate = BASS_FX.BASS_BFX_ROTATE()
                    BASS.BASS_FXGetParameters(effectRotate, rotate)
                    rotate.fRate = rate
                    BASS.BASS_FXSetParameters(effectRotate, rotate)
                }
            } else {
                if (effectRotate != 0) {
                    BASS.BASS_ChannelRemoveFX(channelPlay, effectRotate)
                    effectRotate = 0
                }
            }
        }
    }

    fun setPhaser(params: List<Float>?) {
        if (channelPlay != 0) {
            if (params != null) {
                if (effectPhaser == 0) {
                    effectPhaser = BASS.BASS_ChannelSetFX(channelPlay, BASS_FX.BASS_FX_BFX_PHASER, 0)
                }
                if (effectPhaser != 0) {
                    val phaser = BASS_FX.BASS_BFX_PHASER()
                    BASS.BASS_FXGetParameters(effectPhaser, phaser)
                    phaser.fDryMix = params[0]
                    phaser.fWetMix = params[1]
                    phaser.fFeedback = params[2]
                    phaser.fRate = params[3]
                    phaser.fRange = params[4]
                    phaser.fFreq = params[5]
                    BASS.BASS_FXSetParameters(effectPhaser, phaser)
                }
            } else {
                if (effectPhaser != 0) {
                    BASS.BASS_ChannelRemoveFX(channelPlay, effectPhaser)
                    effectPhaser = 0
                }
            }
        }
    }

    fun setCompressor(params: List<Float>?) {
        if (channelPlay != 0) {
            if (params != null) {
                if (compressorEffects == 0) {
                    compressorEffects = BASS.BASS_ChannelSetFX(channelPlay, BASS_FX_BFX_COMPRESSOR2, 0)
                }
                if (effectPhaser != 0) {
                    val compressor = BASS_FX.BASS_BFX_COMPRESSOR2()
                    BASS.BASS_FXGetParameters(compressorEffects, compressor)
                    compressor.fGain = params[0]
                    compressor.fThreshold = params[1]
                    compressor.fRatio = params[2]
                    compressor.fAttack = params[3]
                    compressor.fRelease = params[4]
                    BASS.BASS_FXSetParameters(compressorEffects, compressor)
                }
            } else {
                if (compressorEffects != 0) {
                    BASS.BASS_ChannelRemoveFX(channelPlay, compressorEffects)
                    compressorEffects = 0
                }
            }
        }
    }

    fun setAutoWah(params: List<Float>?) {
        if (channelPlay != 0) {
            if (params != null) {
                if (autoEffectFx == 0) {
                    autoEffectFx = BASS.BASS_ChannelSetFX(channelPlay, BASS_FX.BASS_FX_BFX_PHASER, 0)
                }
                if (autoEffectFx != 0) {
                    val phaser = BASS_FX.BASS_BFX_PHASER()
                    BASS.BASS_FXGetParameters(autoEffectFx, phaser)
                    phaser.fDryMix = params[0]
                    phaser.fWetMix = params[1]
                    phaser.fFeedback = params[2]
                    phaser.fRate = params[3]
                    phaser.fRange = params[4]
                    phaser.fFreq = params[5]
                    BASS.BASS_FXSetParameters(autoEffectFx, phaser)
                }
            } else {
                if (autoEffectFx != 0) {
                    BASS.BASS_ChannelRemoveFX(channelPlay, autoEffectFx)
                    autoEffectFx = 0
                }
            }
        }
    }

    fun setFlanger(params: List<Float>?) {
        val i = this.channelPlay
        if (i != 0) {
            if (params != null) {
                if (this.effectFlanger == 0) {
                    this.effectFlanger = BASS.BASS_ChannelSetFX(i, BASS_BFX_BQF_NOTCH, 0)
                }
                if (this.effectFlanger != 0) {
                    try {
                        val flanger = BASS.BASS_DX8_FLANGER()
                        BASS.BASS_FXGetParameters(this.effectFlanger, flanger)
                        flanger.fWetDryMix = params[0]
                        flanger.fDepth = params[1]
                        flanger.fFeedback = params[2]
                        flanger.fDelay = params[3]
                        flanger.lPhase = params[4].toInt()
                        if (params.size == 6) {
                            flanger.fFrequency = params[5]
                        }
                        BASS.BASS_FXSetParameters(this.effectFlanger, flanger)
                        return
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return
                    }
                }
                return
            }
            val i2 = this.effectFlanger
            if (i2 != 0) {
                BASS.BASS_ChannelRemoveFX(i, i2)
                this.effectFlanger = 0
            }
        }
    }

    fun setReverse(isReverse: Boolean) {
        this.isReverse = isReverse
        val i = this.channelPlay
        if (i != 0) {
            val tempoSource = BASS_FX.BASS_FX_TempoGetSource(i)
            BASS.BASS_ChannelGetAttribute(tempoSource, BASS_FX.BASS_ATTRIB_REVERSE_DIR, 0.0f)
            if (isReverse) {
                BASS.BASS_ChannelSetAttribute(tempoSource, BASS_FX.BASS_ATTRIB_REVERSE_DIR, -1.0f)
            } else {
                BASS.BASS_ChannelSetAttribute(tempoSource, BASS_FX.BASS_ATTRIB_REVERSE_DIR, 1.0f)
            }
        }
    }

    private fun seekToChannel(i: Int) {
        val i2 = this.channelPlay
        if (i2 != 0) {
            BASS.BASS_ChannelSetPosition(i2, BASS.BASS_ChannelSeconds2Bytes(i2, i.toDouble()), 0)
        }
    }

    private fun getChannelLength(): Int {
        val channelBytes2Seconds: Double
        val i2 = this.channelPlay
        if (i2 == 0) {
            return 0
        }
        channelBytes2Seconds = BASS.BASS_ChannelBytes2Seconds(i2, BASS.BASS_ChannelGetLength(i2, 0))
        return channelBytes2Seconds.toInt()
    }

    companion object {
        private const val TAG = "BASSMediaPlayer"
    }
}