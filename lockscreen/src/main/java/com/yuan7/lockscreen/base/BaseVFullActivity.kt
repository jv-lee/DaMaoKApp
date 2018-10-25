package com.yuan7.lockscreen.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle

/**
 * Created by Administrator on 2018/6/5.
 */
abstract class BaseVFullActivity<DB : ViewDataBinding>(var layoutId: Int) : BaseFullActivity() {

    protected lateinit var binding: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        bindData(savedInstanceState)
    }

    protected abstract fun bindData(savedInstanceState: Bundle?)
}