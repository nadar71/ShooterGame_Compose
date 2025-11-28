package com.indiewalkabout.cosmoraiders

import android.app.Application
import com.indiewalkabout.cosmoraiders.di.appModule
import com.indiewalkabout.cosmoraiders.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(
                appModule,
                platformModule
            )
        }
    }
}