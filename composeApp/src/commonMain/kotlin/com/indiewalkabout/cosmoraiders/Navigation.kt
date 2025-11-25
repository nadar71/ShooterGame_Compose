package com.indiewalkabout.cosmoraiders

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectAsState

sealed class Screen {
    object MainMenu : Screen()
    object Game : Screen()
    data class GameOver(val finalScore: Int, val highScore: Int) : Screen()
}

class NavigationController(initialScreen: Screen) {
    private val _currentScreen = MutableStateFlow<Screen>(initialScreen)
    val currentScreen: StateFlow<Screen> = _currentScreen
    
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }
    
    @Composable
    fun CurrentScreen() {
        val screen by currentScreen.collectAsState()
        
        // Track the last screen to prevent unnecessary recompositions
        val lastScreen = remember { mutableStateOf<Screen?>(null) }
        
        LaunchedEffect(screen) {
            // Only process if the screen has actually changed
            if (screen != lastScreen.value) {
                println("Navigation: Changing screen from ${lastScreen.value} to $screen")
                lastScreen.value = screen
            }
        }
        
        when (val current = screen) {
            is Screen.MainMenu -> {
                // You'll need to implement the main menu screen
                // For now, we'll navigate to game directly after a small delay
                LaunchedEffect(Unit) {
                    if (lastScreen.value == null) { // Only auto-navigate on first launch
                        kotlinx.coroutines.delay(100) // Small delay to prevent race conditions
                        navigateTo(Screen.Game)
                    }
                }
                // Show a simple loading or empty state while transitioning
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading game...")
                }
            }
            is Screen.Game -> {
                MainScreen(
                    onGameOver = { score, highScore ->
                        // Only navigate if we're not already on the GameOver screen
                        if (screen !is Screen.GameOver) {
                            navigateTo(Screen.GameOver(score, highScore))
                        }
                    },
                    onExitToMenu = {
                        // Only navigate if we're not already on the MainMenu
                        if (screen !is Screen.MainMenu) {
                            navigateTo(Screen.MainMenu)
                        }
                    }
                )
            }
            is Screen.GameOver -> {
                GameOverScreen(
                    finalScore = current.finalScore,
                    highScore = current.highScore,
                    onPlayAgain = {
                        // Only navigate if we're not already on the Game screen
                        if (screen !is Screen.Game) {
                            navigateTo(Screen.Game)
                        }
                    },
                    onExit = {
                        // Only navigate if we're not already on the MainMenu
                        if (screen !is Screen.MainMenu) {
                            navigateTo(Screen.MainMenu)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun rememberNavigationController(startDestination: Screen = Screen.MainMenu): NavigationController {
    return remember { NavigationController(startDestination) }
}
