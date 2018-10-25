package com.yuan7.lockscreen.di.component

import android.app.Application

import com.yuan7.lockscreen.LockScreenApp
import com.yuan7.lockscreen.di.module.ActivityBuildersModule
import com.yuan7.lockscreen.di.module.AppModule

import javax.inject.Singleton

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule

/**
 * Created by Administrator on 2018/5/23.
 */
@Singleton
@Component(modules = arrayOf(AndroidInjectionModule::class, ActivityBuildersModule::class, AppModule::class))
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: LockScreenApp)
}
