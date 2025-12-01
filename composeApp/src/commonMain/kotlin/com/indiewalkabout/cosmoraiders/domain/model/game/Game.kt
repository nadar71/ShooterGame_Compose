package com.indiewalkabout.cosmoraiders.domain.model.game

import com.indiewalkabout.cosmoraiders.domain.model.player.Player
import com.indiewalkabout.cosmoraiders.presentation.state.GameStateManager

// Represents the main game instance with all game-related data.
data class Game(
    val score: Int = 0,
    val level: Int = 1,
    val player: Player = Player.create(),
    val settings: GameSettings = GameSettings(),
    val gameStateManager: GameStateManager = GameStateManager()
) {
    // Creates a new game with default settings
    fun newGame(): Game {
        println("Game: Creating new game")
        return copy(
            score = 0,
            level = 1,
            player = Player.create(),
            settings = GameSettings()
        )
    }
    
    // Updates game score and level
    fun updateScoreLevel(score: Int = this.score, level: Int = this.level): Game {
        return copy(score = score, level = level)
    }

    // Updates game score and level
    fun updateScore(score: Int = this.score): Game {
        println("Game: Updating score to: $score")
        return copy(score = score)
    }

    // Updates game score and level
    fun updateLevel(level: Int = this.level): Game {
        println("Game: Updating level to: $level")
        return copy(level = level)
    }
    
    /*// Decreases player's lives and returns true if game over
    fun decreaseLives(amount: Int = 1): Game {
        player.decreaseLives(amount)
        return copy() // Return new instance with updated player state
    }*/
}

