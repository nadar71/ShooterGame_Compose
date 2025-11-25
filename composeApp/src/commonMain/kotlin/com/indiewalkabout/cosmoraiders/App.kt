package com.indiewalkabout.cosmoraiders

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        // Initialize the navigation controller with the main menu as the start destination
        val navController = rememberNavigationController(Screen.MainMenu)
        
        // The NavigationController handles all screen transitions
        navController.CurrentScreen()
    }
}

@Composable
@Preview
fun AppPreview() {
    App()
}