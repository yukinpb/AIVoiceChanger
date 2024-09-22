package com.example.voicechanger.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.voicechanger.base.BaseViewModel
import com.example.voicechanger.model.AudioModel
import com.example.voicechanger.utils.getDuration
import com.example.voicechanger.utils.getSize
import com.example.voicechanger.utils.getVoiceEffectDirPath
import com.example.voicechanger.utils.getVoiceRecordDirPath
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

enum class SortType {
    NAME_ASC, NAME_DESC, DATE_NEWEST, DATE_OLDEST
}

@HiltViewModel
class AudioListViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    private val _audioFiles = MutableLiveData<List<AudioModel>>()
    val audioFiles: LiveData<List<AudioModel>> get() = _audioFiles

    private var sortType: SortType = SortType.DATE_NEWEST

    init {
        loadAudioFiles()
    }

    fun setSortType(newSortType: SortType) {
        sortType = newSortType
        sortAudioFiles()
    }

    fun getSortType() = sortType

    fun loadAudioFiles() {
        val audioFilesList = mutableListOf<AudioModel>()
        val voiceRecordDir = File(context.getVoiceRecordDirPath())
        val voiceEffectDir = File(context.getVoiceEffectDirPath())

        val directories = listOf(voiceEffectDir, voiceRecordDir)

        directories.forEach { dir ->
            if (dir.exists() && dir.isDirectory) {
                val files = dir.listFiles { _, name -> name.endsWith(".mp3") || name.endsWith(".wav") }
                    ?.sortedByDescending { it.lastModified() }

                files?.forEach { file ->
                    if (file.length() > 0) {
                        val size = file.getSize()
                        val duration = file.getDuration()
                        audioFilesList.add(AudioModel(file.absolutePath, file.name, duration, file.lastModified(), size))
                    }
                }
            }
        }

        _audioFiles.postValue(audioFilesList)
    }

    private fun sortAudioFiles() {
        val sortedList = _audioFiles.value?.let { fileList ->
            when (sortType) {
                SortType.NAME_ASC -> fileList.sortedBy { it.fileName }
                SortType.NAME_DESC -> fileList.sortedByDescending { it.fileName }
                SortType.DATE_NEWEST -> fileList.sortedByDescending { it.dateCreate }
                SortType.DATE_OLDEST -> fileList.sortedBy { it.dateCreate }
            }
        } ?: emptyList()

        _audioFiles.postValue(sortedList)
    }
}