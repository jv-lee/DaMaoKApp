package com.yuan7.lockscreen.model.repository

import android.app.Activity
import android.graphics.Bitmap

import com.yuan7.lockscreen.base.mvvm.BaseRepository
import com.yuan7.lockscreen.model.db.AppDataBase
import com.yuan7.lockscreen.model.entity.LabelDB
import com.yuan7.lockscreen.utils.BitmapUtil
import com.yuan7.lockscreen.utils.FileUtil
import com.yuan7.lockscreen.utils.WallpaperUtil

import javax.inject.Inject
import javax.inject.Singleton

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Administrator on 2018/5/30.
 */
@Singleton
class WallpaperRepository @Inject
constructor(service: AppDataBase) : BaseRepository<AppDataBase>(service) {

    fun getWallpaperSetResponse(activity: Activity, entity: LabelDB, sBitmap: Bitmap?): Observable<Boolean> {
        return Observable.create { e: ObservableEmitter<Boolean>? ->
            if (entity.type == 1) {
                WallpaperUtil.setPortraitWallpaper(activity, BitmapUtil.decodeFile(entity.localPath))
            } else {
                val bitmap = BitmapUtil.decodeFile(entity.localPath)
                WallpaperUtil.setLandscapeWallpaper(activity, bitmap, sBitmap ?: bitmap)
            }
            val count = service.labelDao().queryByHiapkIdCount(entity.hiapkId)
            if (count == 0) {
                service.labelDao().insert(entity)
            }
            e!!.onNext(true)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//        return Observable.create({ e ->
//            if (entity.type == 1) {
//                WallpaperUtil.setPortraitWallpaper(activity, BitmapUtil.decodeFile(entity.localPath))
//            } else {
//                val bitmap = BitmapUtil.decodeFile(entity.localPath)
//                WallpaperUtil.setLandscapeWallpaper(activity, bitmap, sBitmap ?: bitmap)
//            }
//            val count = service.labelDao().queryByHiapkIdCount(entity.hiapkId)
//            if (count == 0) {
//                service.labelDao().insert(entity)
//            }
//            e.onNext(true)
//        } as ObservableOnSubscribe<Boolean>).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
    }

    fun downloadWallpaper(entity: LabelDB): Observable<Boolean> {
        return Observable.create { e: ObservableEmitter<Boolean>? ->
            val count = service.labelDao().queryByHiapkIdCount(entity.hiapkId)
            if (count == 0) {
                service.labelDao().insert(entity)
                e!!.onNext(true)
            } else {
                e!!.onNext(false)
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//        return Observable.create({ e ->
//            val count = service.labelDao().queryByHiapkIdCount(entity.hiapkId)
//            if (count == 0) {
//                service.labelDao().insert(entity)
//                e.onNext(true)
//            } else {
//                e.onNext(false)
//            }
//        } as ObservableOnSubscribe<Boolean>).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())

    }

    fun delete(entity: LabelDB): Observable<Int> {
        return Observable.create { e: ObservableEmitter<Int>? ->
            FileUtil.delete(entity.localPath)
            e!!.onNext(service.labelDao().delete(entity))
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//        return Observable.create({ e ->
//            FileUtil.delete(entity.localPath)
//            e.onNext(service.labelDao().delete(entity))
//        } as ObservableOnSubscribe<Int>)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
    }

}
