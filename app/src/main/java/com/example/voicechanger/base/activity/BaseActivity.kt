package com.example.voicechanger.base.activity

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.example.voicechanger.base.BaseViewModel

abstract class BaseActivity<BD : ViewDataBinding, VM : BaseViewModel> :
    BaseActivityNotRequireViewModel<BD>() {

    private lateinit var viewModel: VM

    abstract fun getVM(): VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getVM()
    }

}