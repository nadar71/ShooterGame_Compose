package com.indiewalkabout.cosmoraiders.domain.model

import com.indiewalkabout.cosmoraiders.domain.model.game.GameSettings
import com.indiewalkabout.cosmoraiders.domain.model.player.Player
import com.indiewalkabout.cosmoraiders.presentation.state.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Manages the game state and data
class GameManager(
    initialScore: Int = 0,
    initialLevel: Int = 1,
    initialHighScore: Int = 0,
    val player: Player = Player.create(),
    val settings: GameSettings = GameSettings(),
    initialState: GameState = GameState.MainMenu
) {
    // Game state management
    private val _currentState = MutableStateFlow<GameState>(initialState)
    val currentState: StateFlow<GameState> = _currentState.asStateFlow()
    
    // Game data
    private val _score = MutableStateFlow(initialScore)
    val score: StateFlow<Int> = _score.asStateFlow()
    
    private var _highScore = initialHighScore
    private var _currentLevel = initialLevel

    // Public getters
    val highScore: Int get() = _highScore
    val currentLevel: Int get() = _currentLevel
    
    // Backing property to access the current score value internally
    private val currentScore: Int get() = _score.value

    init {
        // Load saved high score from persistence (to be implemented)
        // _highScore = loadHighScore()
    }

    // Game state management methods
    fun startNewGame() {
        _score.value = 0
        _currentLevel = 1
        _currentState.value = GameState.Playing
    }

    fun resetGame() {
        _score.value = 0
        _currentLevel = 1
        player.reset()
        _currentState.value = GameState.MainMenu
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
        _currentState.value = GameState.LevelComplete(_currentLevel, _score.value)
        _currentLevel++
        println("GameManager: Level updated to: $_currentLevel")
    }

    fun gameOver() {
        if (_score.value > _highScore) {
            _highScore = _score.value
            // saveHighScore(_highScore) // To be implemented with data persistence
        }
        println("GameManager: Publishing GameOver state. Score: ${_score.value}, High Score: $_highScore")
        // Ensure we're on the main thread when updating the state
        CoroutineScope(Dispatchers.Main).launch {
            _currentState.emit(GameState.GameOver(_score.value, _highScore))
        }
    }

    // UI state management
    fun showMainMenu() {
        _currentState.value = GameState.MainMenu
    }

    fun showSettings() {
        _currentState.value = GameState.Settings
    }

    fun showTutorial() {
        _currentState.value = GameState.Tutorial
    }

    // Game data manipulation
    fun addScore(scorePoints: Int) {
        _score.value += scorePoints
        println("GameManager: Added $scorePoints points. New score: ${_score.value}")
    }


    // Helper functions
    fun isInState(vararg states: GameState): Boolean {
        return states.any { it::class == _currentState.value::class }
    }

    // Creates a new game with default settings
    fun newGame(): GameManager {
        println("GameManager: Creating new game")
        return GameManager(
            initialScore = 0,
            initialLevel = 1,
            player = Player.create(),
            settings = GameSettings(),
            initialState = GameState.Playing
        )
    }

    // Creates a copy of the current game state with updated values
    fun copy(
        score: Int = this._score.value,
        level: Int = this._currentLevel,
        highScore: Int = this._highScore,
        player: Player = this.player,
        settings: GameSettings = this.settings,
        state: GameState = this._currentState.value
    ): GameManager {
        return GameManager(
            initialScore = score,
            initialLevel = level,
            initialHighScore = highScore,
            player = player,
            settings = settings,
            initialState = state
        )
    }
}
