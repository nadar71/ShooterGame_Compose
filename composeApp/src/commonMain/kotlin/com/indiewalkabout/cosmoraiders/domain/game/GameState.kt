package com.indiewalkabout.cosmoraiders.domain.game

// different game states
sealed class GameState {
    object Initializing : GameState()
    object MainMenu : GameState()
    object Loading : GameState()
    object Playing : GameState()
    object Paused : GameState()
    data class LevelComplete(val level: Int, val score: Int) : GameState()
    data class GameOver(val finalScore: Int, val highScore: Int) : GameState()
    object Settings : GameState()
    object Tutorial : GameState()
    
    // Checks if the game is in a state where the main game loop should be active.
    val isGameActive: Boolean
        get() = this is Playing
    
    // Checks if the game is in a state where the UI should be shown.
    val shouldShowUI: Boolean
        get() = this !is Loading
}

// @deprecated Use GameState instead for more granular state management
enum class GameStatus {
    Idle,
    Started,
    Over
}