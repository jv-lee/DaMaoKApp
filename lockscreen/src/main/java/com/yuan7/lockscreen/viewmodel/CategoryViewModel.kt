package com.yuan7.lockscreen.viewmodel

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer

import com.yuan7.lockscreen.base.mvvm.BaseAdapterViewModel
import com.yuan7.lockscreen.model.entity.CategoryEntity
import com.yuan7.lockscreen.model.entity.Data
import com.yuan7.lockscreen.model.entity.Status
import com.yuan7.lockscreen.model.repository.CategoryRepository
import com.yuan7.lockscreen.view.adapter.CategoryAdapter

import javax.inject.Inject

/**
 * Created by Administrator on 2018/5/29.
 */

class CategoryViewModel @Inject
constructor(dataRepository: CategoryRepository, application: Application) : BaseAdapterViewModel<CategoryRepository, CategoryAdapter>(dataRepository, application) {
    var categoryObservable: LiveData<Data<CategoryEntity>>? = null

    override fun bindData() {
        categoryObservable = dataRepository.categorys
    }

    fun observable() {
        status.set(Status.LOADING)
        categoryObservable!!.observe(owner, Observer {
            if (it == null) {
                status.set(Status.ERROR)
                return@Observer
            }
            status.set(Status.SUCCESS)
            adapter!!.replaceData(it!!.rows)
        })
    }

}
