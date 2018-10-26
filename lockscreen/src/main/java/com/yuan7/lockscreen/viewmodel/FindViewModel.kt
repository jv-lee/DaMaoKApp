package com.yuan7.lockscreen.viewmodel

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.yuan7.lockscreen.base.mvvm.BaseViewModel
import com.yuan7.lockscreen.model.entity.SearchEntity
import com.yuan7.lockscreen.model.repository.FindRepository
import javax.inject.Inject

/**
 * Created by Administrator on 2018/6/13.
 */
class FindViewModel @Inject constructor(dataRepository: FindRepository, application: Application) : BaseViewModel<FindRepository>(dataRepository, application) {
    var searchObservable: LiveData<List<SearchEntity>>? = null
    var value: MutableLiveData<Int>? = null

    override fun bindData() {
        this.value = MutableLiveData()

        searchObservable = Transformations.switchMap(value!!, { input ->
            if (input == 0) {
                ABSENT
            }
            dataRepository.searchTag
        })
    }

    fun setValue(value: Int) {
        this.value!!.value = value
    }

    companion object {
        private val ABSENT = MutableLiveData<List<SearchEntity>>()
    }

}