package com.yuan7.lockscreen.model.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.helper.donwload.DownloadApi
import com.yuan7.lockscreen.helper.donwload.ProgressHelper

import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

import javax.inject.Inject
import javax.inject.Singleton

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Administrator on 2018/5/29.
 */
@Singleton
class DownloadRepository @Inject
constructor() {

    fun getFile(path: String): LiveData<File> {
        val data = MutableLiveData<File>()

        val retrofitBuilder = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://yuan7ad.oss-cn-shenzhen.aliyuncs.com")
        val builder = ProgressHelper.addProgress(null)
        val retrofit = retrofitBuilder
                .client(builder.build())
                .build().create(DownloadApi::class.java!!)

        retrofit.retrofitDownload(path).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val `is` = response.body().byteStream()
                    val fileDir = File(Constants.DIR_CONSTANT)
                    if (!fileDir.exists()) {
                        fileDir.mkdir()
                    }
                    val file = File(Constants.DIR_CONSTANT, path.substring(path.lastIndexOf("/") + 1, path.length))
                    if (file.exists()) {
                        file.delete()
                    }
                    val fos = FileOutputStream(file)
                    val bis = BufferedInputStream(`is`)
                    val buffer = ByteArray(1024)
                    var len = 0

                    while (bis.read(buffer).apply { len = this } > 0) {
                        fos.write(buffer, 0, len)
                        fos.flush()
                    }
//                    while ((len = bis.read(buffer)) != -1) {
//                        fos.write(buffer, 0, len)
//                        fos.flush()
//                    }
                    data.setValue(file)
                    fos.close()
                    bis.close()
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                    data.setValue(null)
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                data.setValue(null)
            }
        })
        return data
    }
}
