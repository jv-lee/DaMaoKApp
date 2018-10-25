package com.yuan7.lockscreen.model.service

import com.yuan7.lockscreen.model.entity.*
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by Administrator on 2018/5/25.
 */

interface APIService {

    @get:GET("getSearch")
    val search: Call<Response<List<SearchEntity>>>

    @FormUrlEncoded
    @POST("getLabelHiapks")
    fun getLabels(@Field("page") page: Int, @Field("pageNumber") pageNumber: Int, @Field("labelId") labelId: Int, @Field("type") type: Int, @Field("imei") imei: String): Call<Response<Data<LabelEntity>>>

    @FormUrlEncoded
    @POST("getCategorys")
    fun getCategorys(@Field("page") page: Int, @Field("pageNumber") pageNumber: Int): Call<Response<Data<CategoryEntity>>>

    @FormUrlEncoded
    @POST("getCategoryHiapks")
    fun getCategoryLabels(@Field("page") page: Int, @Field("pageNumber") pageNumber: Int, @Field("categoryId") categoryId: Int, @Field("type") type: Int, @Field("imei") imei: String): Call<Response<Data<LabelEntity>>>

    @FormUrlEncoded
    @POST("getHiapks")
    fun getSearchLabels(@Field("page") page: Int, @Field("pageNumber") pageNumber: Int, @Field("value") value: String, @Field("type") type: Int, @Field("imei") imei: String): Call<Response<Data<LabelEntity>>>

    @FormUrlEncoded
    @POST("iniDevice")
    fun init(@Field("imei") imei: String, @Field("height") height: Int, @Field("width") width: Int): Call<Response<InitEntity>>

    companion object {
        val BASE_API = "http://119.23.136.190:9080/server/"
    }
}
