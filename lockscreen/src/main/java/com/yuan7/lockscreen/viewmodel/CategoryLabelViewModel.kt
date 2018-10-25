package com.yuan7.lockscreen.viewmodel

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import com.yuan7.lockscreen.base.mvvm.BaseAdapterViewModel
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.helper.a.AHelper
import com.yuan7.lockscreen.helper.a.Call
import com.yuan7.lockscreen.model.entity.Data
import com.yuan7.lockscreen.model.entity.LabelEntity
import com.yuan7.lockscreen.model.entity.Status
import com.yuan7.lockscreen.model.repository.CategoryLabelRepository
import com.yuan7.lockscreen.utils.SPUtil
import com.yuan7.lockscreen.view.adapter.LabelListAdapter
import javax.inject.Inject

/**
 * Created by Administrator on 2018/6/15.
 */
class CategoryLabelViewModel @Inject constructor(dataRepository: CategoryLabelRepository, application: Application) : BaseAdapterViewModel<CategoryLabelRepository, LabelListAdapter>(dataRepository, application) {
    var pageNo = 1

    var labelObservable: LiveData<Data<LabelEntity>>? = null
    var page: MutableLiveData<Int>? = null
    var categoryId: MutableLiveData<Int>? = null
    var type: MutableLiveData<Int>? = null

    init {
        ABSENT.value = null
    }

    override fun bindData() {
        this.page = MutableLiveData()
        this.categoryId = MutableLiveData()
        this.type = MutableLiveData()

        labelObservable = Transformations.switchMap(page, { input ->
            if (input != null) {
                ABSENT
            }
            dataRepository.getCategoryLabel(page!!.value!!, categoryId!!.value!!, type!!.value!!)
        })
    }

    fun observable() {
        status.set(Status.LOADING)
        labelObservable!!.observe(owner, Observer {
            AHelper.getNativeExpressAD(getApplication(), object : Call {
                override fun responseEntity(entity: LabelEntity?) {
                    val enable = SPUtil.get(Constants.GAME_ENABLE_YQ, false) as Boolean
                    if (enable) {
                        if (page!!.value!! % 2 == 0) {
                            if (it!!.rows != null && it!!.rows.size != 0 && entity != null && page!!.value!! < it!!.countPage) {
                                it!!.rows.add(entity)
                            }
                        }
                    }

                    //adapter
                    if (it == null) {
                        status.set(Status.ERROR)
                        adapter!!.loadMoreFail()
                        return
                    }
                    if (page!!.value!! > it!!.countPage) {
                        adapter!!.loadMoreEnd()
                        status.set(Status.SUCCESS)
                        return
                    }
                    if (page!!.value!! == 1) {
                        adapter!!.setNewData(it!!.rows)
                    } else {
                        adapter!!.data.addAll(it!!.rows)
                    }
                    adapter!!.notifyDataSetChanged()
                    adapter!!.loadMoreComplete()
                    status.set(Status.SUCCESS)

                }
            })
        })
    }

    fun loadFirst() {
        pageNo = 1
        this.categoryId!!.value = activity.intent.getIntExtra(Constants.CATEGORY_ID, 0)
        this.type!!.value = activity.intent.getIntExtra(Constants.SCREEN, 0)
        this.page!!.value = pageNo
    }

    fun loadMore() {
        pageNo += 1
        this.categoryId!!.value = activity.intent.getIntExtra(Constants.CATEGORY_ID, 0)
        this.type!!.value = activity.intent.getIntExtra(Constants.SCREEN, 0)
        this.page!!.value = pageNo
    }

    companion object {
        var ABSENT = MutableLiveData<Data<LabelEntity>>()
    }
}