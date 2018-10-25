package com.yuan7.lockscreen.di.module


import com.yuan7.lockscreen.view.activity.*

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Administrator on 2018/5/23.
 */
@Module
abstract class ActivityBuildersModule {
    @ContributesAndroidInjector(modules = arrayOf(FragmentBuildersModule::class))
    internal abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = arrayOf(FragmentBuildersModule::class))
    internal abstract fun contributeFindActivity(): FindActivity

    @ContributesAndroidInjector(modules = arrayOf(FragmentBuildersModule::class))
    internal abstract fun contributeSearchActivity(): SearchActivity

    @ContributesAndroidInjector(modules = arrayOf(FragmentBuildersModule::class))
    internal abstract fun contributeWallpaperActivity(): WallpaperActivity

    @ContributesAndroidInjector(modules = arrayOf(FragmentBuildersModule::class))
    internal abstract fun contributeLabelListActivity(): LabelListActivity

    @ContributesAndroidInjector(modules = arrayOf(FragmentBuildersModule::class))
    internal abstract fun contributeImageActivity(): ImageActivity

    @ContributesAndroidInjector(modules = arrayOf(FragmentBuildersModule::class))
    internal abstract fun contributeLockActivity(): LockActivity

    @ContributesAndroidInjector(modules = arrayOf(FragmentBuildersModule::class))
    internal abstract fun contributeWallpaperLocalActivity(): WallpaperLocalActivity

    @ContributesAndroidInjector(modules = arrayOf(FragmentBuildersModule::class))
    internal abstract fun contributeLocalActivity(): LocalActivity

    @ContributesAndroidInjector(modules = arrayOf(FragmentBuildersModule::class))
    internal abstract fun contributeWelcomeActivity(): WelcomeActivity

}
