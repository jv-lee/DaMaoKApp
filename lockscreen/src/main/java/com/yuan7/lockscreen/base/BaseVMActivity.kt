package com.yuan7.lockscreen.base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import javax.inject.Inject

/**
 * Created by Administrator on 2018/6/5.
 */

abstract class BaseVMActivity<DB : ViewDataBinding, VM : ViewModel>(var layoutId: Int,var vmClass:Class<VM>) : BaseActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected lateinit var binding: DB
    protected lateinit var viewModel: VM

    protected abstract fun bindData(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,layoutId)
        viewModel = ViewModelProviders.of(this,viewModelFactory).get(vmClass)
        bindData(savedInstanceState)
    }

}
