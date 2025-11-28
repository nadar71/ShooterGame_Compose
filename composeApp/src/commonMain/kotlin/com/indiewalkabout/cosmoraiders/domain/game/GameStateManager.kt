package com.indiewalkabout.cosmoraiders.domain.game

import com.indiewalkabout.cosmoraiders.domain.enemy.Enemy
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

// Manages the game state and handles state transitions.
class GameStateManager {
    private val _currentState = MutableStateFlow<GameState>(GameState.MainMenu)
    val currentState: StateFlow<GameState> = _currentState.asStateFlow()
        /*.stateIn(
            scope = CoroutineScope(Dispatchers.Main + Job()),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GameState.MainMenu
        )*/

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

    // Decreases player's lives and checks if game over condition is met
    fun decreaseLives(game: Game): Boolean {
        game.player.decreaseLives()
        println("GameStateManager: Decreasing lives. remaining: ${game.player.lives}")
        return game.player.lives <= 0
    }

     // Checks if the game should be over based on:
     // - Enemies that have gone off-screen (decreases lives)
     // - Enemies that have collided with the player (decreases lives)
     // - Player's lives reaching zero

    // Checks for game over conditions and cleans up off-screen enemies
    fun checkGameOver(
        game: Game,
        enemies: MutableList<Enemy>,
        playerX: Float,
        playerY: Float,
        playerRadius: Float,
        screenHeight: Int,
        onGameOver: () -> Unit = {}
    ): Boolean {
        // Track enemies that need to be removed
        val enemiesToRemove = mutableListOf<Enemy>()
        var playerHit = false

        enemies.forEach { enemy ->
            // Check if enemy went off-screen
            if ((enemy.y.value ?: 0f) > screenHeight) {
                println("Enemy went off-screen: $enemy")
                enemiesToRemove.add(enemy)
            } 
            // Check if enemy collided with player
            else {
                val dx = playerX - enemy.x
                val dy = playerY - (enemy.y.value ?: 0f)
                val distance = kotlin.math.hypot(dx, dy).toDouble()

                if (distance < (playerRadius + enemy.radius)) {
                    println("Collision detected with enemy: $enemy")
                    enemiesToRemove.add(enemy)
                    playerHit = true
                }
            }
        }
        
        // Remove all enemies that need to be cleaned up
        enemies.removeAll(enemiesToRemove)
        
        // Handle player hit
        if (playerHit) {
            val shouldGameOver = decreaseLives(game)
            if (shouldGameOver) {
                enemies.clear()
                gameOver()
                onGameOver()
                return true
            }
            game.player.markLifeLostProcessed()
        }
        
        return false
    }
}