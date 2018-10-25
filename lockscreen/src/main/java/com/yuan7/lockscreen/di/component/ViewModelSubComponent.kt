package com.yuan7.lockscreen.di.component

import com.yuan7.lockscreen.viewmodel.*
import dagger.Subcomponent

/**
 * Created by Administrator on 2018/5/18.
 */
@Subcomponent
interface ViewModelSubComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): ViewModelSubComponent
    }

    fun labelViewModel(): LabelViewModel
    fun categoryViewModel(): CategoryViewModel
    fun findViewModel(): FindViewModel
    fun localViewModel(): LocalViewModel
    fun searchViewModel(): SearchLabelViewModel
    fun categoryLabelViewModel(): CategoryLabelViewModel
    fun downloadViewModel(): DownloadViewModel

}
