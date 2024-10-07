package com.example.voicechanger.utils

object Constants {
    const val DEFAULT_TIMEOUT = 30
    const val DIRECTORY = "DIRECTORY"
    const val ARG_AUDIO_PATH = "ARG_AUDIO_PATH"
    const val ARG_AUDIO_MODEL = "ARG_AUDIO_MODEL"
    object Preferences {
        const val PREF_FILE_NAME = "Preferences"
    }

    object Timing {
        const val DURATION_TIME_CLICKABLE = 500L
    }

    object Directories {
        const val VOICE_CHANGER_DIR = "VoiceChanger"
        const val VOICE_RECORDER_DIR = "VoiceRecorder"
        const val FOLDER_TEMP = ".temp"
    }

    object NetworkRequestCode {
        const val REQUEST_CODE_200 = 200    //normal
        const val REQUEST_CODE_400 = 400    //parameter error
        const val REQUEST_CODE_401 = 401    //unauthorized error
        const val REQUEST_CODE_403 = 403
        const val REQUEST_CODE_404 = 404    //No data error
    }

    object ApiComponents {
        const val BASE_URL = "https://api.fakeyou.com/tts/"
    }

    object Fragments {
        const val AI_VOICE_MAKER_FRAGMENT = "AI_VOICE_MAKER_FRAGMENT"
        const val OPEN_FILE_FRAGMENT = "OPEN_FILE_FRAGMENT"
        const val MY_VOICE_FRAGMENT = "MY_AUDIO_FRAGMENT"
        const val AUDIO_LIST_FRAGMENT = "AUDIO_LIST_FRAGMENT"
        const val TRIM_AUDIO_FRAGMENT = "TRIM_AUDIO_FRAGMENT"
        const val MERGE_AUDIO_FRAGMENT = "MERGE_AUDIO_FRAGMENT"
        const val CHANGE_EFFECT_FRAGMENT = "CHANGE_EFFECT_FRAGMENT"
    }

    object MediaConstants {
        const val aacType = "aac"
        const val flacType = "flac"
        const val midType = "mid"
        const val mp3Type = "mp3"
        const val oggType = "ogg"
        const val wavType = "wav"
        const val wmaType = "wma"
    }
}
