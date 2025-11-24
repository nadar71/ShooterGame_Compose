package com.indiewalkabout.cosmoraiders.domain.game

import com.indiewalkabout.cosmoraiders.domain.player.Player

/**
 * Represents the main game instance with all game-related data.
 */
data class Game(
    val score: Int = 0,
    val level: Int = 1,
    val player: Player = Player.create(),
    val settings: GameSettings = GameSettings(),
    val gameStateManager: GameStateManager = GameStateManager()
) {
    // Creates a new game with default settings
    fun newGame(): Game {
        return copy(
            score = 0,
            level = 1,
            player = Player.create(),
            settings = GameSettings()
        )
    }
    
    // Updates the game with new score and level
    fun update(score: Int = this.score, level: Int = this.level): Game {
        return copy(score = score, level = level)
    }
    
    // Decreases player's lives and returns true if game over
    fun decreaseLives(amount: Int = 1): Game {
        player.decreaseLives(amount)
        return copy() // Return new instance with updated player state
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

