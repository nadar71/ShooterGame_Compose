package com.indiewalkabout.cosmoraiders.domain.game

import com.indiewalkabout.cosmoraiders.PLAYER_LIVES


// Represents the main game instance with all game-related data.
data class Game(
    val score: Int = 0,
    val level: Int = 1,
    val lives: Int = PLAYER_LIVES,  // Default number of lives
    var isLifeLost: Boolean = false,
    val settings: GameSettings = GameSettings(),
    val gameStateManager: GameStateManager = GameStateManager()
) {
    // Creates a new game with default settings
    fun newGame(): Game = copy(
        score = 0,
        level = 1,
        lives = PLAYER_LIVES,  // Reset lives to default
        settings = GameSettings()
    )

    // Decreases player's lives and returns the updated game state
    fun decreaseLives(amount: Int = 1): Game {
        println("Game: decreaseLives: Decreasing lives $lives by $amount")
        return if (lives > amount) {
            copy(lives = lives - amount)
        } else {
            copy(lives = 0)
        }
    }
    
    // Updates the game with new score and level
    fun update(score: Int = this.score, level: Int = this.level): Game {
        return copy(score = score, level = level)
    }
}

// Contains all the game settings that affect gameplay.
data class GameSettings(
    val playerSpeed: Float = 15f,
    val weaponSpeed: Float = 20f,
    val targetSpeed: Float = 30f,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val difficulty: Difficulty = Difficulty.NORMAL
)

// Represents different difficulty levels
enum class Difficulty(val multiplier: Float) {
    EASY(0.7f),
    NORMAL(1.0f),
    HARD(1.5f)
}

