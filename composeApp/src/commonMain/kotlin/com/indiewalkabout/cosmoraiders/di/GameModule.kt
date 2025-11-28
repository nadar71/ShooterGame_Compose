package com.indiewalkabout.cosmoraiders.di

import com.indiewalkabout.cosmoraiders.PLAYER_LIVES
import com.indiewalkabout.cosmoraiders.domain.game.Difficulty
import com.indiewalkabout.cosmoraiders.domain.game.Game
import com.indiewalkabout.cosmoraiders.domain.game.GameSettings
import com.indiewalkabout.cosmoraiders.domain.game.GameStateManager
import com.indiewalkabout.cosmoraiders.domain.player.Player
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
            difficulty = Difficulty.NORMAL
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
