package com.indiewalkabout.cosmoraiders

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.indiewalkabout.cosmoraiders.navigation.AppNavigation
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}

@Composable
@Preview
fun AppPreview() {
    App()
}