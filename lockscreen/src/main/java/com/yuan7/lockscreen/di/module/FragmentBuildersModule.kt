package com.yuan7.lockscreen.di.module


import com.yuan7.lockscreen.view.fragment.*

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Administrator on 2018/5/23.
 */
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    internal abstract fun contributePortraitFragment(): PortraitFragment

    @ContributesAndroidInjector
    internal abstract fun contributeLandscapeFragment(): LandscapeFragment

    @ContributesAndroidInjector
    internal abstract fun contributeSearchFragment(): FindFragment

    @ContributesAndroidInjector
    internal abstract fun contributeMeFragment(): MeFragment

    @ContributesAndroidInjector
    internal abstract fun contributeLabelFragment(): LabelFragment

    @ContributesAndroidInjector
    internal abstract fun contributeCategoryFragment(): CategoryFragment

    @ContributesAndroidInjector
    internal abstract fun contributeLocalFragment(): LocalFragment

    @ContributesAndroidInjector
    internal abstract fun contributeDownloadDialogFragment(): DownloadDialogFragment

    @ContributesAndroidInjector
    internal abstract fun contributeImageLandscapeFragment(): ImageLandscapeFragment

    @ContributesAndroidInjector
    internal abstract fun contributeImagePortraitFragment(): ImagePortraitFragment

    @ContributesAndroidInjector
    internal abstract fun contributeSearchLabelFragment(): SearchLabelFragment
}
