package com.indiewalkabout.cosmoraiders.presentation.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Manages the game state and handles state transitions.
class GameStateManager {
    private val _currentState = MutableStateFlow<GameState>(GameState.MainMenu)
    val currentState: StateFlow<GameState> = _currentState.asStateFlow()


    private var _score = 0
    private var _highScore = 0
    private var _currentLevel = 1

    val score: Int get() = _score
    val highScore: Int get() = _highScore
    val currentLevel: Int get() = _currentLevel

    init {
        // Load saved high score from persistence (to be implemented)
        // _highScore = loadHighScore()

        // Start in the main menu
        showMainMenu()
    }

    // State transition methods
    fun startNewGame() {
        _score = 0
        _currentLevel = 1
        _currentState.value = GameState.Playing
    }

    fun pauseGame() {
        if (_currentState.value is GameState.Playing) {
            _currentState.value = GameState.Paused
        }
    }

    fun resumeGame() {
        if (_currentState.value is GameState.Paused) {
            _currentState.value = GameState.Playing
        }
    }

    fun completeLevel() {
        _currentState.value = GameState.LevelComplete(_currentLevel, _score)
        _currentLevel++
    }

    fun gameOver() {
        if (_score > _highScore) {
            _highScore = _score
            // saveHighScore(_highScore) // To be implemented with data persistence
        }
        println("GameStateManager: Publishing GameOver state. Score: $_score, High Score: $_highScore")
        // Ensure we're on the main thread when updating the state
        CoroutineScope(Dispatchers.Main).launch {
            _currentState.emit(GameState.GameOver(_score, _highScore))
        }
    }

    fun showMainMenu() {
        _currentState.value = GameState.MainMenu
    }

    fun showSettings() {
        _currentState.value = GameState.Settings
    }

    fun showTutorial() {
        _currentState.value = GameState.Tutorial
    }

    fun addScore(points: Int) {
        _score += points
    }

    fun resetGame() {
        _score = 0
        _currentLevel = 1
        _currentState.value = GameState.MainMenu
    }

    // Helper function to check current state
    fun isInState(vararg states: GameState): Boolean {
        return states.any { it::class == _currentState.value::class }
    }

}