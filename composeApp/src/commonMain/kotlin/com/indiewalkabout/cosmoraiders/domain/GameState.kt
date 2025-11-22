package com.indiewalkabout.cosmoraiders.domain

import androidx.compose.runtime.Immutable

/*
enum class GameStatus {
    Idle,
    Started,
    Over
}*/


// All possible game states
sealed class GameState {
    // initial state: main menu
    @Immutable
    data object Idle : GameState()

    // Game is currently being played
    @Immutable
    data class Playing(
        val score: Int = 0,           // current score
        val level: Int = 1,           // current level
        val lives: Int = 3,           // remaining lives
        val isPaused: Boolean = false // game is currently paused?
    ) : GameState()

    // Game has ended
    @Immutable
    data class GameOver(
        val finalScore: Int,         // final score
        val highScore: Int? = null,  // highest score if any
        val levelReached: Int        // highest level reached
    ) : GameState()

    // Game is showing level completion screen
    @Immutable
    data class LevelComplete(
        val level: Int,     // level that  just completed
        val nextLevel: Int, // next level to play
        val score: Int      // current score
    ) : GameState()
}

// Actions that can be performed on the game state
sealed class GameAction {
    data object StartGame : GameAction()
    data object PauseGame : GameAction()
    data object ResumeGame : GameAction()
    data object EndGame : GameAction()
    data object CompleteLevel : GameAction()
    data object StartNextLevel : GameAction()
    data class UpdateScore(val points: Int) : GameAction()
    data class LoseLife(val remainingLives: Int) : GameAction()
    data class SetPaused(val isPaused: Boolean) : GameAction()
}

