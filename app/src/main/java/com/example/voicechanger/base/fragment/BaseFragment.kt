package com.example.voicechanger.base.fragment

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.example.voicechanger.base.BaseViewModel

abstract class BaseFragment<BD : ViewDataBinding, VM : BaseViewModel>(@LayoutRes id: Int) :
    BaseFragmentNotRequireViewModel<BD>(id) {

    private lateinit var viewModel: VM

    abstract fun getVM(): VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getVM()
    }

    override fun initView(savedInstanceState: Bundle?) {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            showHideLoading(it)
        }

    }

}