package com.indiewalkabout.cosmoraiders.domain.model.game

import com.indiewalkabout.cosmoraiders.data.local.enum.DifficultyLevel

// Contains all the game settings that affect gameplay.
data class GameSettings(
    val playerSpeed: Float = 15f,
    val weaponSpeed: Float = 20f,
    val targetSpeed: Float = 30f,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val difficultyLevel: DifficultyLevel = DifficultyLevel.NORMAL
)