package com.yuan7.lockscreen.viewmodel

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import com.yuan7.lockscreen.base.mvvm.BaseAdapterViewModel
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.model.entity.LabelDB
import com.yuan7.lockscreen.model.entity.Status
import com.yuan7.lockscreen.model.repository.LocalRepository
import com.yuan7.lockscreen.view.adapter.LocalListAdapter
import javax.inject.Inject

/**
 * Created by Administrator on 2018/6/15.
 */
class LocalViewModel @Inject constructor(repository: LocalRepository, application: Application) : BaseAdapterViewModel<LocalRepository, LocalListAdapter>(repository, application) {
    private var pageNo = 0

    var labelObservable: LiveData<List<LabelDB>>? = null
    var page: MutableLiveData<Int>? = null
    var type: MutableLiveData<Int>? = null

    init {
        ABSENT.value = null
    }

    override fun bindData() {
        this.page = MutableLiveData()
        this.type = MutableLiveData()

        labelObservable = Transformations.switchMap(page!!, { input ->
            if (input == null) {
                ABSENT
            }
            dataRepository.findLabelBytype(type!!.value!!, page!!.value!!)
        })
    }

    fun deleteEntity(entity: LabelDB): LiveData<LabelDB> {
        var data = MutableLiveData<LabelDB>()
        dataRepository.delete(entity)
                .subscribe {
                    if (it > 0) {
                        data.value = entity
                    } else {
                        data.value = null
                    }
                }
        return data
    }

    fun observable() {
        status.set(Status.LOADING)

        labelObservable!!.observe(owner, Observer {
            if (it == null) {
                status.set(Status.ERROR)
                adapter!!.loadMoreFail()
                return@Observer
            }

            if (it!!.size == 0) {
                adapter!!.loadMoreEnd()
                status.set(Status.SUCCESS)
                return@Observer
            }

            if (page!!.value!! == 0) {
                adapter!!.setNewData(it)
            } else {
                adapter!!.data.addAll(it!!)
            }
            adapter!!.notifyDataSetChanged()
            adapter!!.loadMoreComplete()
            status.set(Status.SUCCESS)

        })
    }

    fun loadFist() {
        pageNo = 0
        this.type!!.value = fragment.arguments.getInt(Constants.SCREEN)
        this.page!!.value = pageNo
    }

    fun loadMore() {
        pageNo += 15
        this.type!!.value = fragment.arguments.getInt(Constants.SCREEN)
        this.page!!.value = pageNo
    }

    companion object {
        private val ABSENT = MutableLiveData<List<LabelDB>>()
    }
}