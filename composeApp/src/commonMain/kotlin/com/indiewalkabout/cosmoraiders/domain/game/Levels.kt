package com.indiewalkabout.cosmoraiders.domain.game

// Represents game levels with their target scores
enum class GameLevel(val score: Int) {
    ONE(score   = 10),
    TWO(score   = 20),
    THREE(score = 30),
    FOUR(score  = 40),
    FIVE(score  = 50);

    companion object {
        // Gets the GameLevel enum value based on the level number (1-based index)
        fun fromLevelNumber(level: Int): GameLevel {
            return values().getOrElse(level - 1) { ONE }
        }
    }
}

// different game levels settings
val levelSettings = listOf(
    GameSettings(playerSpeed = 1f, weaponSpeed = 1f, targetSpeed = 5f),
    GameSettings(playerSpeed = 1.2f, weaponSpeed = 1.2f, targetSpeed = 6f),
    GameSettings(playerSpeed = 1.4f, weaponSpeed = 1.4f, targetSpeed = 7f),
    GameSettings(playerSpeed = 1.6f, weaponSpeed = 1.6f, targetSpeed = 8f),
    GameSettings(playerSpeed = 1.8f, weaponSpeed = 1.8f, targetSpeed = 9f)
)

// Maps game levels to their corresponding settings
val levels = listOf(
    GameLevel.ONE to levelSettings[0],
    GameLevel.TWO to levelSettings[1],
    GameLevel.THREE to levelSettings[2],
    GameLevel.FOUR to levelSettings[3],
    GameLevel.FIVE to levelSettings[4]
)