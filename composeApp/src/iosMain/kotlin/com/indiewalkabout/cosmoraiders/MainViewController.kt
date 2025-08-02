package com.indiewalkabout.cosmoraiders

import androidx.compose.ui.window.ComposeUIViewController
import com.indiewalkabout.cosmoraiders.App
import com.indiewalkabout.cosmoraiders.di.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) { App() }