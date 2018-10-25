package com.yuan7.lockscreen.model.service

import com.yuan7.lockscreen.model.entity.ResponseEntity

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by Administrator on 2018/6/14.
 */

interface EnableService {

    @POST("sdkserver/daiji/appconfig")
    fun doPostGameEnable(@Body params: RequestBody): Call<ResponseEntity>

    companion object {
        val BASE_API = "http://120.77.128.96:8088/"
    }
}
