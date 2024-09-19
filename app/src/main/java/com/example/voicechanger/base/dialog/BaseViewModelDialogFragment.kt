package com.example.voicechanger.base.dialog

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.example.voicechanger.base.BaseViewModel

abstract class BaseViewModelDialogFragment<BD : ViewDataBinding, VM : BaseViewModel>(@LayoutRes id: Int) :
    BaseViewBindingDialogFragment<BD>(id) {

    private lateinit var viewModel: VM

    abstract fun getVM(): VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getVM()
    }

}