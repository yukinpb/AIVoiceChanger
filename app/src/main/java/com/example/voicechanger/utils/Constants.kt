package com.example.voicechanger.utils

object Constants {
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

    object VoiceEffects {
        const val NONE = 0
        const val RADIO = 1
        const val CHIPMUNK = 2
        const val ROBOT = 3
        const val CAVE = 4
    }

    object UI {
        const val NUMBER_OF_LAYER_VOICE_CIRCLE = 20
    }

    object RingtoneOptions {
        const val RINGTONE = "Ringtone"
        const val ALARM = "Alarm"
        const val NOTIFICATION = "Notification"
    }
}
