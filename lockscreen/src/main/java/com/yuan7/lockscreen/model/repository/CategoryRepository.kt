package com.yuan7.lockscreen.model.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

import com.yuan7.lockscreen.base.mvvm.BaseRepository
import com.yuan7.lockscreen.model.entity.CategoryEntity
import com.yuan7.lockscreen.model.entity.Data
import com.yuan7.lockscreen.model.entity.Response
import com.yuan7.lockscreen.model.service.APIService

import javax.inject.Inject
import javax.inject.Singleton

import retrofit2.Call
import retrofit2.Callback

/**
 * Created by Administrator on 2018/5/29.
 */
@Singleton
class CategoryRepository @Inject
constructor(service: APIService) : BaseRepository<APIService>(service) {

    val categorys: LiveData<Data<CategoryEntity>>
        get() {
            val data = MutableLiveData<Data<CategoryEntity>>()

            service.getCategorys(1, 20).enqueue(object : Callback<Response<Data<CategoryEntity>>> {
                override fun onResponse(call: Call<Response<Data<CategoryEntity>>>, response: retrofit2.Response<Response<Data<CategoryEntity>>>) {
                    data.value = if (response.body() == null) null else response.body().obj
                }

                override fun onFailure(call: Call<Response<Data<CategoryEntity>>>, t: Throwable) {
                    data.value = null
                }
            })
            return data
        }
}
