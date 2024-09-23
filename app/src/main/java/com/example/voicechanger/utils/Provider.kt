package com.example.voicechanger.utils

import com.example.voicechanger.R
import com.example.voicechanger.model.AudioAttrModel
import com.example.voicechanger.model.LanguageModel
import java.util.Locale

object LanguageProvider {
    fun getLanguages(): List<LanguageModel> {
        return listOf(
            LanguageModel(R.mipmap.ic_english, "English", "(English)", "en"),
            LanguageModel(R.mipmap.ic_portuguese, "Portuguese", "(Português)", "pt"),
            LanguageModel(R.mipmap.ic_france, "French", "(Français)", "fr"),
            LanguageModel(R.mipmap.ic_german, "German", "(Deutsch)", "de"),
            LanguageModel(R.mipmap.ic_hindi, "Hindi", "(हिंदी)", "hi"),
            LanguageModel(R.mipmap.ic_china, "Chinese", "(汉语)", "zh"),
            LanguageModel(R.mipmap.ic_spanish, "Spanish", "(Española)", "es")
        )
    }
}

object EffectProvider {
    fun getEffects(): List<AudioAttrModel> {
        return listOf(
            AudioAttrModel(0, "Normal", "asetrate=44100"),
            AudioAttrModel(1, "Helium", "asetrate=44100*1.122,atempo=1.122"),
            AudioAttrModel(2, "Robot", "asetrate=44100*1.122,atempo=1.122, aecho=0.8:0.88:6:0.4, volume=10dB"),
            AudioAttrModel(3, "Cave", "aecho=1.0:0.7:60:0.25, aresample=44100"),
            AudioAttrModel(4, "Monster", "asetrate=44100*0.889, atempo=0.75, aecho=0.8:0.9:1000:0.3, volume=10dB"),
            AudioAttrModel(5, "Nervous", "asetrate=44100*1.1, atempo=1.5"),
            AudioAttrModel(6, "Drunk", "asetrate=44100*0.888, atempo=0.55"),
            AudioAttrModel(7, "Squirrel", "asetrate=44100*1.122, atempo=2"),
            AudioAttrModel(8, "Child", "asetrate=44100*1.122"),
            AudioAttrModel(9, "Death", "asetrate=44100*0.888, atempo=0.75, volume=5dB, aecho=0.7:0.7:50:0.4"),
            AudioAttrModel(10, "Reverse", "areverse"),
            AudioAttrModel(11, "Grand Canyon", "aecho=0.6:0.6:70:0.5, aresample=44100*0.75"),
            AudioAttrModel(12, "Hexafluorid", "asetrate=44100*0.888"),
            AudioAttrModel(13, "Big Robot", "asetrate=44100*0.94, atempo=0.89, volume=10dB, aecho=0.5:0.5:40:0.3"),
            AudioAttrModel(14, "Telephone", "volume=3dB, lowpass=1300, equalizer=f=1300:t=q:w=15:g=8"),
            AudioAttrModel(15, "Underwater", "volume=3dB, lowpass=400, equalizer=f=400:t=q:w=15:g=8"),
            AudioAttrModel(16, "Extraterrestrial", "asetrate=44100*0.94, atempo=0.65"),
            AudioAttrModel(17, "Villain", "volume=10dB, lowpass=1000"),
            AudioAttrModel(18, "Zombie", "asetrate=44100*0.888, atempo=0.5, chorus=1:0.35:0.4:1:10:10"),
            AudioAttrModel(19, "Megaphone", "volume=15dB, equalizer=f=125:t=q:w=18:g=-10, equalizer=f=400:t=q:w=18:g=15, equalizer=f=4000:t=q:w=18:g=-10, adistort=5"),
            AudioAttrModel(20, "Alien", "asetrate=44100*1.06, atempo=0.9, chorus=0.9:0.35:0.5:1:50:100"),
            AudioAttrModel(21, "Small Alien", "asetrate=44100*1.122, atempo=0.9, chorus=0.9:0.35:0.5:1:50:100"),
            AudioAttrModel(22, "Back Chipmunk", "chorus=0.9:-0.2:0.5:1:400:400"),
            AudioAttrModel(23, "Voice in Factory", "volume=20dB, chorus=0.3:0.4:0.5:1:10:5"),
            AudioAttrModel(24, "Storm Wind", "asetrate=44100*1.06, atempo=1.5, volume=20dB, chorus=0.9:-0.4:0.5:1:2:1"),
            AudioAttrModel(25, "Motorcycle", "asetrate=44100*0.98, aecho=0.5:0.5:30:0.3, chorus=0.9:0.45:0.5:1:100:25"),
            AudioAttrModel(26, "Autowash", "autowah=0.5:1.5:0.5:5:5.3:50"),
            AudioAttrModel(27, "Volume Envelope", "aecho=0.6:0.6:20:0.4, aphaser=0.999:-0.999:-0.6:0.2:6:100"),
            AudioAttrModel(28, "Parody", "chorus=0.9:0.35:0.5:1:400:200"),
            AudioAttrModel(29, "Basso Singer", "equalizer=f=125:t=q:w=18:g=15, equalizer=f=400:t=q:w=18:g=-10, equalizer=f=4000:t=q:w=18:g=-10, aecho=0.8:0.8:1000:0.3"),
            AudioAttrModel(30, "Tenor Singer", "equalizer=f=125:t=q:w=18:g=-10, equalizer=f=400:t=q:w=18:g=-10, equalizer=f=4000:t=q:w=18:g=15, highpass=f=300, aecho=0.8:0.8:1000:0.3"),
            AudioAttrModel(31, "Mr Panic", "asetrate=44100*1.04, atempo=1.3, equalizer=f=125:t=q:w=18:g=-10, equalizer=f=400:t=q:w=18:g=15, equalizer=f=4000:t=q:w=18:g=-10, aphaser=0.999:-0.999:-0.6:0.2:6:100, aecho=0.8:0.8:2000:0.4"),
            AudioAttrModel(32, "Dancing Ghost", "asetrate=44100*0.888, atempo=1.5, chorus=0.9:0.25:0.2:1:50:400, aecho=0.6:0.6:30:0.3, aresample=44100")
        )
    }
}