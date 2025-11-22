package com.indiewalkabout.cosmoraiders.domain

/**
 * Represents the main game instance with all game-related data.
 * This class should be used in conjunction with GameStateManager for state management.
 */
data class Game(
    val score: Int = 0,
    val level: Int = 1,
    val settings: GameSettings = GameSettings(),
    val gameStateManager: GameStateManager = GameStateManager()
) {
    /**
     * Creates a new game with default settings
     */
    fun newGame(): Game = copy(
        score = 0,
        level = 1,
        settings = GameSettings()
    )
    
    /**
     * Updates the game with new score and level
     */
    fun update(score: Int = this.score, level: Int = this.level): Game {
        return copy(score = score, level = level)
    }
}

/**
 * Contains all the game settings that affect gameplay.
 */
data class GameSettings(
    val ninjaSpeed: Float = 15f,
    val weaponSpeed: Float = 20f,
    val targetSpeed: Float = 30f,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val difficulty: Difficulty = Difficulty.NORMAL
)

/**
 * Represents different difficulty levels
 */
enum class Difficulty(val multiplier: Float) {
    EASY(0.7f),
    NORMAL(1.0f),
    HARD(1.5f)
}

/**
 * Represents game levels with their target scores
 */
enum class GameLevel(val score: Int) {
    ONE(score = 10),
    TWO(score = 20),
    THREE(score = 30),
    FOUR(score = 40),
    FIVE(score = 50);
    
    companion object {
        /**
         * Gets the GameLevel enum value based on the level number (1-based index)
         */
        fun fromLevelNumber(level: Int): GameLevel {
            return values().getOrElse(level - 1) { ONE }
        }
    }
}

/**
 * Predefined level settings for different game levels
 */
val levelSettings = listOf(
    GameSettings(ninjaSpeed = 1f, weaponSpeed = 1f, targetSpeed = 5f),
    GameSettings(ninjaSpeed = 1.2f, weaponSpeed = 1.2f, targetSpeed = 6f),
    GameSettings(ninjaSpeed = 1.4f, weaponSpeed = 1.4f, targetSpeed = 7f),
    GameSettings(ninjaSpeed = 1.6f, weaponSpeed = 1.6f, targetSpeed = 8f),
    GameSettings(ninjaSpeed = 1.8f, weaponSpeed = 1.8f, targetSpeed = 9f)
)

/**
 * Maps game levels to their corresponding settings
 */
val levels = listOf(
    GameLevel.ONE to levelSettings[0],
    GameLevel.TWO to levelSettings[1],
    GameLevel.THREE to levelSettings[2],
    GameLevel.FOUR to levelSettings[3],
    GameLevel.FIVE to levelSettings[4]
)