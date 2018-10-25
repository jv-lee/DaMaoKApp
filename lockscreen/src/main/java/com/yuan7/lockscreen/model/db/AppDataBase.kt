package com.yuan7.lockscreen.model.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

import com.yuan7.lockscreen.model.db.dao.LabelDao
import com.yuan7.lockscreen.model.entity.*

/**
 * Created by Administrator on 2018/5/23.
 */
@Database(entities = arrayOf(LabelDB::class), version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun labelDao(): LabelDao
}
