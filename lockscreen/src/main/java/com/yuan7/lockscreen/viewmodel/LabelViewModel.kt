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
import com.yuan7.lockscreen.model.repository.LabelRepository
import com.yuan7.lockscreen.view.adapter.LabelListAdapter
import javax.inject.Inject
import com.yuan7.lockscreen.model.entity.*
import com.yuan7.lockscreen.utils.SPUtil

/**
 * Created by Administrator on 2018/6/11.
 */
class LabelViewModel @Inject constructor(dataRepository: LabelRepository, application: Application) : BaseAdapterViewModel<LabelRepository, LabelListAdapter>(dataRepository, application) {
    private var pageNo = 1

    var labelObservable: LiveData<Data<LabelEntity>>? = null
    var page: MutableLiveData<Int>? = null
    var labelId: MutableLiveData<Int>? = null
    var type: MutableLiveData<Int>? = null

    init {
        ABSENT.value = null
    }

    override fun bindData() {
        this.page = MutableLiveData()
        this.labelId = MutableLiveData()
        this.type = MutableLiveData()

        labelObservable = Transformations.switchMap(page, { input ->
            if (input == 0) {
                ABSENT
            }
            dataRepository.getLabels(page!!.value!!, labelId!!.value!!, type!!.value!!)
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

                    if (page!!.value!! > it!!.countPage!!) {
                        adapter!!.loadMoreEnd()
                        status.set(Status.SUCCESS)
                        return
                    }

                    if (page!!.value!! == 1) {
                        adapter!!.setNewData(it!!.rows)
                    } else {
                        adapter!!.data += it!!.rows!!
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
        this.labelId!!.value = fragment!!.arguments.getInt(Constants.LABEL)
        this.type!!.value = fragment!!.arguments.getInt(Constants.SCREEN)
        this.page!!.value = pageNo
    }

    fun loadMore() {
        this.labelId!!.value = fragment.arguments.getInt(Constants.LABEL)
        this.type!!.value = fragment.arguments.getInt(Constants.SCREEN)
        this.page!!.value = ++pageNo
    }

    companion object {
        private val ABSENT = MutableLiveData<Data<LabelEntity>>()
    }

}