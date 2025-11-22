package com.indiewalkabout.cosmoraiders.domain

/*
data class Game(
    val status: GameStatus = GameStatus.Idle,
    val score: Int = 0,
    val settings: GameSettings = GameSettings()
)

data class GameSettings(
    val ninjaSpeed: Float = 15f,
    val weaponSpeed: Float = 20f,
    val targetSpeed: Float = 30f
)

enum class GameLevel(val score: Int) {
    One(score = 10),
    Two(score = 20),
    Three(score = 30),
    Four(score = 40),
    Five(score = 50)
}

val levelSettings = listOf(
    GameSettings(ninjaSpeed = 1f, weaponSpeed = 1f, targetSpeed = 5f),
    GameSettings(ninjaSpeed = 0f, weaponSpeed = 1f, targetSpeed = 6f),
    GameSettings(ninjaSpeed = 0f, weaponSpeed = 1f, targetSpeed = 7f),
    GameSettings(ninjaSpeed = 0f, weaponSpeed = 0f, targetSpeed = 8f),
    GameSettings(ninjaSpeed = 0f, weaponSpeed = 0f, targetSpeed = 9f),
)

val levels = listOf(
    GameLevel.One to levelSettings[0],
    GameLevel.Two to levelSettings[1],
    GameLevel.Three to levelSettings[2],
    GameLevel.Four to levelSettings[3],
    GameLevel.Five to levelSettings[4],
)*/


// Main game class that manages the game state and settings
data class Game(
    val stateMachine: GameStateMachine = GameStateMachine(),
    val settings: GameSettings = GameSettings()
) {
    // Process a game action and update the game state
    fun processAction(action: GameAction): GameState {
        return stateMachine.processAction(action)
    }

    // Get the current game state
    fun getCurrentState(): GameState {
        return stateMachine.getCurrentState()
    }

    // Check if the game is currently in playing state
    fun isPlaying(): Boolean {
        return stateMachine.getCurrentState() is GameState.Playing
    }

    // Check if the game is currently paused
    fun isPaused(): Boolean {
        return when (val state = stateMachine.getCurrentState()) {
            is GameState.Playing -> state.isPaused
            else -> false
        }
    }

    // Get the current score
    fun getScore(): Int {
        return when (val state = stateMachine.getCurrentState()) {
            is GameState.Playing -> state.score
            is GameState.LevelComplete -> state.score
            is GameState.GameOver -> state.finalScore
            else -> 0
        }
    }

    //Get the current level
    fun getLevel(): Int {
        return when (val state = stateMachine.getCurrentState()) {
            is GameState.Playing -> state.level
            is GameState.LevelComplete -> state.level
            is GameState.GameOver -> state.levelReached
            else -> 1
        }
    }
}

// Game settings
data class GameSettings(
    val ninjaSpeed: Float = 15f,
    val weaponSpeed: Float = 20f,
    val targetSpeed: Float = 30f
)

// Game levels with
enum class GameLevel(val score: Int) {
    One(score = 10),
    Two(score = 20),
    Three(score = 30),
    Four(score = 40),
    Five(score = 50)
}

// Level settings
val levelSettings = listOf(
    GameSettings(ninjaSpeed = 1f, weaponSpeed = 1f, targetSpeed = 5f),
    GameSettings(ninjaSpeed = 0f, weaponSpeed = 1f, targetSpeed = 6f),
    GameSettings(ninjaSpeed = 0f, weaponSpeed = 1f, targetSpeed = 7f),
    GameSettings(ninjaSpeed = 0f, weaponSpeed = 0f, targetSpeed = 8f),
    GameSettings(ninjaSpeed = 0f, weaponSpeed = 0f, targetSpeed = 9f),
)

// Mapping of game levels to their settings as Pairs
// val levels = GameLevel.values().zip(levelSettings)
val levels = listOf(
    GameLevel.One to levelSettings[0],
    GameLevel.Two to levelSettings[1],
    GameLevel.Three to levelSettings[2],
    GameLevel.Four to levelSettings[3],
    GameLevel.Five to levelSettings[4],
)
