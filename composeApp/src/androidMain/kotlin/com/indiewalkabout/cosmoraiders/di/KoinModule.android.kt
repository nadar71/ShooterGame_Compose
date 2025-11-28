package com.indiewalkabout.cosmoraiders.di

import android.content.Context
import com.indiewalkabout.cosmoraiders.domain.audio.AudioPlayer
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

// Android-specific Koin module.
// contains all the Android-specific dependencies.
@OptIn(ExperimentalResourceApi::class)
actual val platformModule = module {
    // Android context
    single<Context> { androidContext() }
    
    // Audio player
    single<AudioPlayer> { AudioPlayer(context = get()) }
}