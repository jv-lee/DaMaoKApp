package com.yuan7.lockscreen

import android.app.Activity
import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.lockstudio.sticklocker.application.LockApplication
import com.umeng.commonsdk.UMConfigure
import com.yuan7.lockscreen.di.AppInjector
import com.yuan7.lockscreen.helper.glide.GlideUtils
import com.yuan7.lockscreen.utils.ParameterUtil
import com.yuan7.lockscreen.utils.SPUtil
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * Created by Administrator on 2018/6/8.
 */
class LockScreenApp : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this@LockScreenApp)

        UMConfigure.init(this, BuildConfig.umeng_key, BuildConfig.umeng_channl, UMConfigure.DEVICE_TYPE_PHONE, null)
        UMConfigure.setLogEnabled(true)

        LockApplication.getInstance().onCreate(this)

        GlideUtils.getInstance(applicationContext)
        SPUtil.getInstance(applicationContext)
        Config.imei = ParameterUtil.getSimpleIMEI(applicationContext)

        com.lgvomp.ovshkk.umsygm.uwaluc.start(applicationContext, "GN251", 0)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}