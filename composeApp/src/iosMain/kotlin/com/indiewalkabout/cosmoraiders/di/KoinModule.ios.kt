package com.indiewalkabout.cosmoraiders.di

import com.indiewalkabout.cosmoraiders.domain.audio.AudioPlayer
import org.koin.dsl.module


// iOS-specific Koin module.
// contains all the iOS-specific dependencies.
actual val platformModule = module {
    // Audio player
    single<AudioPlayer> { AudioPlayer() }
}