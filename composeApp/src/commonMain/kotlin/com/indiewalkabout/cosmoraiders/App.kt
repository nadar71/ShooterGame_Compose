package com.indiewalkabout.cosmoraiders

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.indiewalkabout.cosmoraiders.presentation.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        MainScreen()
    }
}