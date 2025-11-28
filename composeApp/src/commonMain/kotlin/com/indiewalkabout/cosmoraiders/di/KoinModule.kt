package com.indiewalkabout.cosmoraiders.di

import org.koin.core.Koin
import org.koin.dsl.module
import org.koin.mp.KoinPlatform.getKoin

// Common Koin module that contains all the shared dependencies.
val appModule = module {
    includes(gameModule)
}


// Helper function to get the Koin instance.
// This can be used to manually retrieve dependencies if needed.
val koin: Koin get() = getKoin() // org.koin.core.context.GlobalContext.get()