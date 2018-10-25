package com.yuan7.lockscreen.model.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

import com.yuan7.lockscreen.Config
import com.yuan7.lockscreen.base.mvvm.BaseRepository
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.model.service.APIService
import com.yuan7.lockscreen.model.entity.*

import javax.inject.Inject
import javax.inject.Singleton

import retrofit2.Call
import retrofit2.Callback

/**
 * Created by Administrator on 2018/5/25.
 */
@Singleton
class LabelRepository @Inject
constructor(service: APIService) : BaseRepository<APIService>(service) {

    fun getLabels(page: Int, labelId: Int, type: Int): LiveData<Data<LabelEntity>> {
        val data = MutableLiveData<Data<LabelEntity>>()
        service.getLabels(page, Constants.PAGE_NUMBER, labelId, type, Config.imei).enqueue(object : Callback<Response<Data<LabelEntity>>> {
            override fun onResponse(call: Call<Response<Data<LabelEntity>>>, response: retrofit2.Response<Response<Data<LabelEntity>>>) {
                data.value = if (response.body() == null) null else response.body().obj
            }

            override fun onFailure(call: Call<Response<Data<LabelEntity>>>, t: Throwable) {
                data.value = null
            }
        })
        return data
    }
}
