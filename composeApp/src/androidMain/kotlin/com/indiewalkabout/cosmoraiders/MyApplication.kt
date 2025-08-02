package com.indiewalkabout.cosmoraiders

import android.app.Application
import com.indiewalkabout.cosmoraiders.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin {
            androidContext(this@MyApplication)
        }
    }
}