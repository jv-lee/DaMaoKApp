package com.yuan7.lockscreen.viewmodel

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.yuan7.lockscreen.base.mvvm.BaseViewModel
import com.yuan7.lockscreen.model.repository.DownloadRepository
import java.io.File
import javax.inject.Inject

/**
 * Created by Administrator on 2018/6/15.
 */
class DownloadViewModel @Inject constructor(dataRepository: DownloadRepository, application: Application) : BaseViewModel<DownloadRepository>(dataRepository, application) {
    var fileObservable: LiveData<File>? = null
    var path: MutableLiveData<String>? = null

    init {
        ABSENT.value = null
    }

    override fun bindData() {
        this.path = MutableLiveData()
        fileObservable = Transformations.switchMap(path, { input ->
            if (input == null) {
                ABSENT
            }
            dataRepository.getFile(path!!.value!!)
        })
    }

    fun setPath(path: String) {
        this.path!!.value = path
    }

    companion object {
        val ABSENT = MutableLiveData<File>()
    }
}