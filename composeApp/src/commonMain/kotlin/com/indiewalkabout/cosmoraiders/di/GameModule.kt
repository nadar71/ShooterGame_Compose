package com.indiewalkabout.cosmoraiders.di

import com.indiewalkabout.cosmoraiders.domain.game.Game
import com.indiewalkabout.cosmoraiders.domain.game.GameStateManager
import org.koin.dsl.module

val gameModule = module {
    // Single instance of Game that will live for the entire app lifecycle
    single { 
        Game(
            score = 0,
            level = 1,
            player = get(),
            settings = get(),
            gameStateManager = get()
        )
    }
    
    // Single instance of GameStateManager
    single { 
        GameStateManager()
    }
    
    // Game will be available for injection
    single { get<Game>().gameStateManager }
}
