package com.yuan7.lockscreen.model.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.yuan7.lockscreen.model.entity.*
import com.yuan7.lockscreen.constant.DBConstant

import io.reactivex.Flowable

/**
 * Created by Administrator on 2018/5/30.
 */
@Dao
interface LabelDao {

    @Query(DBConstant.FIND_LABEL_BY_TYPE_ALL)
    fun query(type: Int, offset: Int): Flowable<List<LabelDB>>

    @Query(DBConstant.FIND_LABEL_BY_HIAPKID_COUNT)
    fun queryByHiapkIdCount(hiapkId: Int): Int

    @Insert
    fun insert(vararg users: LabelDB)

    @Delete
    fun delete(vararg users: LabelDB): Int

    @Update
    fun update(vararg users: LabelDB)
}
