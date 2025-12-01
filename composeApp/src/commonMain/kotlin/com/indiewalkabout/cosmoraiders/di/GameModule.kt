package com.indiewalkabout.cosmoraiders.di

import com.indiewalkabout.cosmoraiders.data.local.enum.DifficultyLevel
import com.indiewalkabout.cosmoraiders.domain.model.game.Game
import com.indiewalkabout.cosmoraiders.domain.model.game.GameSettings
import com.indiewalkabout.cosmoraiders.presentation.state.GameStateManager
import com.indiewalkabout.cosmoraiders.domain.model.player.Player
import org.koin.dsl.module

val gameModule = module {
    // Player instance
    single { Player() }
    
    // Game settings
    single {
        GameSettings(
            playerSpeed = 15f,
            weaponSpeed = 20f,
            targetSpeed = 30f,
            soundEnabled = true,
            musicEnabled = true,
            difficultyLevel = DifficultyLevel.NORMAL
        )
    }
    
    // GameStateManager
    single { GameStateManager() }
    
    // Game instance
    single {
        Game(
            score = 0,
            level = 1,
            player = get(),
            settings = get(),
            gameStateManager = get()
        )
    }
}
