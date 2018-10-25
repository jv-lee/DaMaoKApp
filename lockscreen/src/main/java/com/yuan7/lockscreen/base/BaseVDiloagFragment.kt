package com.yuan7.lockscreen.base

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * Created by Administrator on 2018/6/6.
 */
abstract class BaseVDiloagFragment<DB : ViewDataBinding>(var layoutId: Int) : DialogFragment(), LifecycleRegistryOwner, HasSupportFragmentInjector {
    protected lateinit var binding: DB
    protected abstract fun bindData(savedInstanceState: Bundle?)
    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>
    private val mLifecycleRegistry = LifecycleRegistry(this)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindData(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return childFragmentInjector
    }

    override fun getLifecycle(): LifecycleRegistry {
        return mLifecycleRegistry
    }
}