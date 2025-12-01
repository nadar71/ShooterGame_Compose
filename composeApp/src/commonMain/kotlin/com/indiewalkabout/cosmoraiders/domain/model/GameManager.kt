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
    private var _score = initialScore
    private var _highScore = initialHighScore
    private var _currentLevel = initialLevel

    // Public getters
    val score: Int get() = _score
    val highScore: Int get() = _highScore
    val currentLevel: Int get() = _currentLevel

    init {
        // Load saved high score from persistence (to be implemented)
        // _highScore = loadHighScore()
    }

    // Game state management methods
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
        println("GameManager: Level updated to: $_currentLevel")
    }

    fun gameOver() {
        if (_score > _highScore) {
            _highScore = _score
            // saveHighScore(_highScore) // To be implemented with data persistence
        }
        println("GameManager: Game Over. Score: $_score, High Score: $_highScore")
        
        CoroutineScope(Dispatchers.Main).launch {
            _currentState.emit(GameState.GameOver(_score, _highScore))
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
        _score += scorePoints
        println("GameManager: Added $scorePoints points. New score: $_score")
    }

    /*fun updateScore(newScore: Int) {
        _score = newScore
        println("GameManager: Score updated to: $_score")
    }*/

    /*fun updateLevel(newLevel: Int) {
        _currentLevel = newLevel
        println("GameManager: Level updated to: $_currentLevel")
    }*/

    fun resetGame() {
        _score = 0
        _currentLevel = 1
        player.reset()
        _currentState.value = GameState.MainMenu
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
        score: Int = this._score,
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
