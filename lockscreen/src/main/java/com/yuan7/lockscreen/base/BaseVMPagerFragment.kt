package com.yuan7.lockscreen.base

import android.arch.lifecycle.*
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
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
abstract class BaseVMPagerFragment<DB : ViewDataBinding, VM : ViewModel>(var layoutId: Int,var vmClass:Class<VM>) : Fragment(), LifecycleRegistryOwner, HasSupportFragmentInjector {
    protected lateinit var binding: DB
    protected lateinit var viewModel: VM
    protected abstract fun bindData(savedInstanceState: Bundle?)
    protected abstract fun lazyLoad()

    private var isVisibleUser = false
    private var isVisibleView = false
    private var firstVisible = true

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>
    private val mLifecycleRegistry = LifecycleRegistry(this)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(vmClass)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindData(savedInstanceState)
        isVisibleView = true
        if (isVisibleView && isVisibleUser && firstVisible) {
            firstVisible = false
            lazyLoad()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            isVisibleUser = true
            //首次用户可见 开始加载数据
            if (isVisibleView && isVisibleToUser && firstVisible) {
                firstVisible = false
                lazyLoad()
            }
        } else {
            isVisibleUser = false
        }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return childFragmentInjector
    }

    override fun getLifecycle(): LifecycleRegistry {
        return mLifecycleRegistry
    }
}