package com.yuan7.lockscreen.di.module

import android.app.Application
import android.arch.lifecycle.ViewModelProvider
import android.arch.persistence.room.Room

import com.yuan7.lockscreen.di.component.ViewModelSubComponent
import com.yuan7.lockscreen.model.db.AppDataBase
import com.yuan7.lockscreen.model.service.APIService
import com.yuan7.lockscreen.model.service.EnableService
import com.yuan7.lockscreen.viewmodel.ViewModelFactory

import java.util.concurrent.TimeUnit

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Administrator on 2018/5/23.
 */
@Module(subcomponents = arrayOf(ViewModelSubComponent::class))
class AppModule {

    @Singleton
    @Provides
    internal fun provideApiService(): APIService {
        return Retrofit.Builder()
                .client(OkHttpClient.Builder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .writeTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .build())
                .baseUrl(APIService.BASE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideEnableService(): EnableService {
        return Retrofit.Builder()
                .client(OkHttpClient.Builder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .writeTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .build())
                .baseUrl(EnableService.BASE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EnableService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideViewModelFactory(viewModelSubComponent: ViewModelSubComponent.Builder): ViewModelProvider.Factory {
        return ViewModelFactory(viewModelSubComponent.build())
    }

    @Singleton
    @Provides
    internal fun provideAppDataBase(app: Application): AppDataBase {
        return Room.databaseBuilder(app, AppDataBase::class.java!!, "database-name").build()
    }

}
