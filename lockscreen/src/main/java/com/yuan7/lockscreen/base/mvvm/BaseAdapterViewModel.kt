package com.yuan7.lockscreen.base.mvvm

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.databinding.ObservableField
import android.support.v4.app.Fragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yuan7.lockscreen.base.binding.BaseBindingAdapter


/**
 * Created by Administrator on 2018/5/25.
 */

abstract class BaseAdapterViewModel<R, A>(protected var dataRepository: R, application: Application) : AndroidViewModel(application) {
    var status = ObservableField<Int>()
    protected lateinit var activity: Activity
    protected lateinit var fragment: Fragment
    protected lateinit var owner: LifecycleOwner
    var adapter: A? = null

    init {
        bindData()
    }

    protected abstract fun bindData()

    fun setParams(fragment: Fragment, owner: LifecycleOwner, adapter: A) {
        this.fragment = fragment
        this.owner = owner
        this.adapter = adapter
    }

    fun setParams(activity: Activity, owner: LifecycleOwner, adapter: A) {
        this.activity = activity
        this.owner = owner
        this.adapter = adapter
    }

}
