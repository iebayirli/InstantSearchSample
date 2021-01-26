package com.huawei.instantsearchsample

import android.app.Application
import com.huawei.instantsearchsample.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class InstantSearchApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@InstantSearchApplication)
            modules(
                networkModule
            )
        }
    }
}