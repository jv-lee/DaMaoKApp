package com.yuan7.lockscreen.model.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

import com.yuan7.lockscreen.base.mvvm.BaseRepository
import com.yuan7.lockscreen.model.db.AppDataBase
import com.yuan7.lockscreen.model.entity.LabelDB
import com.yuan7.lockscreen.model.service.APIService
import com.yuan7.lockscreen.utils.FileUtil

import javax.inject.Inject

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Administrator on 2018/5/31.
 */

class LocalRepository @Inject
constructor(service: AppDataBase) : BaseRepository<AppDataBase>(service) {

    fun findLabelBytype(type: Int, page: Int): LiveData<List<LabelDB>> {
        val data = MutableLiveData<List<LabelDB>>()
        service!!.labelDao().query(type, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { labels -> data.setValue(labels) }
        return data
    }

    fun delete(entity: LabelDB): Observable<Int> {
        return Observable.create { e: ObservableEmitter<Int>? ->
            FileUtil.delete(entity.localPath)
            e!!.onNext(service!!.labelDao().delete(entity))
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
