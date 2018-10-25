package com.yuan7.lockscreen.model.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

import com.yuan7.lockscreen.base.mvvm.BaseRepository
import com.yuan7.lockscreen.model.entity.Response
import com.yuan7.lockscreen.model.entity.SearchEntity
import com.yuan7.lockscreen.model.service.APIService

import javax.inject.Inject

import retrofit2.Call
import retrofit2.Callback

/**
 * Created by Administrator on 2018/6/4.
 */

class FindRepository @Inject
constructor(service: APIService) : BaseRepository<APIService>(service) {

    val searchTag: LiveData<List<SearchEntity>>
        get() {
            val data = MutableLiveData<List<SearchEntity>>()
            service.search.enqueue(object : Callback<Response<List<SearchEntity>>> {
                override fun onResponse(call: Call<Response<List<SearchEntity>>>, response: retrofit2.Response<Response<List<SearchEntity>>>) {
                    data.value = if (response.body() == null) null else response.body().obj
                }

                override fun onFailure(call: Call<Response<List<SearchEntity>>>, t: Throwable) {
                    data.value = null
                }
            })
            return data
        }
}
