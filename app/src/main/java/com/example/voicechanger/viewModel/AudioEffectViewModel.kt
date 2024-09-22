package com.example.voicechanger.viewModel

import androidx.lifecycle.MutableLiveData
import com.example.voicechanger.base.BaseViewModel
import com.example.voicechanger.model.AudioEffectModel
import com.example.voicechanger.repository.AudioEffectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AudioEffectViewModel @Inject constructor(
    private val repository: AudioEffectRepository
) : BaseViewModel() {

    private val _audioEffect = MutableLiveData<List<AudioEffectModel>>()
    val audioEffect = _audioEffect

    fun getAllEffect() {
        _audioEffect.postValue(repository.getAllEffect())
    }

    fun getRobotEffect() {
        _audioEffect.postValue(repository.getRobotEffect())
    }

    fun getPeopleEffect() {
        _audioEffect.postValue(repository.getPeopleEffect())
    }

    fun getScaryEffect() {
        _audioEffect.postValue(repository.getScaryEffect())
    }

    fun getOtherEffect() {
        _audioEffect.postValue(repository.getOtherEffect())
    }
}