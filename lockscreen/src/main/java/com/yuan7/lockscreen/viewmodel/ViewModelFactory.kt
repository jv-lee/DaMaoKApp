package com.yuan7.lockscreen.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.v4.util.ArrayMap

import com.yuan7.lockscreen.di.component.ViewModelSubComponent
import java.util.concurrent.Callable

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Administrator on 2018/5/18.
 */
@Singleton
class ViewModelFactory @Inject
constructor(viewModelSubComponent: ViewModelSubComponent) : ViewModelProvider.Factory {
    private val creators: ArrayMap<Class<*>, Callable<out ViewModel>>

    init {
        creators = ArrayMap<Class<*>, Callable<out ViewModel>>()

        creators.put(LabelViewModel::class.java, Callable { viewModelSubComponent.labelViewModel() })
        creators.put(CategoryViewModel::class.java, Callable { viewModelSubComponent.categoryViewModel() })
        creators.put(FindViewModel::class.java, Callable { viewModelSubComponent.findViewModel() })
        creators.put(LocalViewModel::class.java, Callable { viewModelSubComponent.localViewModel() })
        creators.put(SearchLabelViewModel::class.java, Callable { viewModelSubComponent.searchViewModel() })
        creators.put(CategoryLabelViewModel::class.java, Callable { viewModelSubComponent.categoryLabelViewModel() })
        creators.put(DownloadViewModel::class.java, Callable { viewModelSubComponent.downloadViewModel() })
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator: Callable<out ViewModel>? = creators[modelClass]
        if (creator == null) {
            for ((key, value) in creators) {
                if (modelClass.isAssignableFrom(key)) {
                    creator = value
                    break
                }
            }
        }
        if (creator == null) {
            throw IllegalArgumentException("Unknown model class $modelClass")
        }
        try {
            return creator.call() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }
}
