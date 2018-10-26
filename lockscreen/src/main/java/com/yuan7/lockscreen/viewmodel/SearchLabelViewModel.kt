package com.yuan7.lockscreen.viewmodel

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import com.yuan7.lockscreen.base.mvvm.BaseAdapterViewModel
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.model.entity.Data
import com.yuan7.lockscreen.model.entity.LabelEntity
import com.yuan7.lockscreen.model.entity.Status
import com.yuan7.lockscreen.model.repository.SearchLabelRepository
import com.yuan7.lockscreen.view.adapter.LabelListAdapter
import javax.inject.Inject

/**
 * Created by Administrator on 2018/6/15.
 */
class SearchLabelViewModel @Inject constructor(repository: SearchLabelRepository, application: Application) : BaseAdapterViewModel<SearchLabelRepository, LabelListAdapter>(repository, application) {
    var pageNo = 1
    var labelObservable: LiveData<Data<LabelEntity>>? = null
    var params: MutableLiveData<Params>? = null

    init {
        ABSENT.value = null
    }

    override fun bindData() {
        this.params = MutableLiveData()

        labelObservable = Transformations.switchMap(params!!, { input ->
            if (input == null) {
                ABSENT
            }
            dataRepository.getSearchLabels(params!!.value!!.page, params!!.value!!.value, params!!.value!!.type)
        })
    }

    fun observable() {
        status.set(Status.SUCCESS)
        labelObservable!!.observe(owner, Observer {
            if (it == null) {
                status.set(Status.ERROR)
                adapter!!.loadMoreFail()
                return@Observer
            }

            status.set(Status.SUCCESS)
            if (it!!.countPage == 0) {
                adapter!!.data.clear()
                adapter!!.notifyDataSetChanged()
                adapter!!.loadMoreComplete()
                return@Observer
            }

            if (params!!.value!!.page > it!!.countPage) {
                adapter!!.loadMoreEnd()
                return@Observer
            }

            if (params!!.value!!.page == 1) {
                adapter!!.setNewData(it!!.rows)
            } else {
                adapter!!.data!!.addAll(it!!.rows)
            }
            adapter!!.notifyDataSetChanged()
            adapter!!.loadMoreComplete()
        })
    }

    fun loadFirst() {
        pageNo = 1
        this.params!!.value = Params(fragment.activity.intent.getStringExtra(Constants.ACTIVITY_INTENT_SEARCH), fragment.arguments.getInt(Constants.SCREEN), pageNo)
    }

    fun loadMore() {
        pageNo += 1
        this.params!!.value = Params(fragment.activity.intent.getStringExtra(Constants.ACTIVITY_INTENT_SEARCH), fragment.arguments.getInt(Constants.SCREEN), pageNo)
    }

    class Params(internal var value: String, internal var type: Int, internal var page: Int)

    companion object {
        val ABSENT = MutableLiveData<Data<LabelEntity>>()
    }

}