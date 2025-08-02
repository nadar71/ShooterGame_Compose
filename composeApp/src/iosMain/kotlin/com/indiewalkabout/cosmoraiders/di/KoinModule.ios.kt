package com.indiewalkabout.cosmoraiders.di

import com.indiewalkabout.cosmoraiders.domain.audio.AudioPlayer
import org.koin.dsl.module

actual val targetModule = module {
    single<AudioPlayer> { AudioPlayer() }
}