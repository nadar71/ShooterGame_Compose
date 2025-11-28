package com.indiewalkabout.cosmoraiders.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

// Initialize Koin for all platforms.
fun initKoin(
    enableNetworkLogs: Boolean = false,
    appDeclaration: KoinAppDeclaration = {}
): KoinApplication {
    return startKoin {
        appDeclaration()
        modules(
            appModule,
            platformModule
        )
    }
}

// Platform-specific module that needs to be implemented in each platform.
// Used for platform-specific dependency injection.
expect val platformModule: org.koin.core.module.Module
