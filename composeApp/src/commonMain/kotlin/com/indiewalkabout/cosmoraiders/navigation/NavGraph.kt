package com.indiewalkabout.cosmoraiders.navigation

sealed class Screen(val route: String) {
    object MainMenu : Screen("main_menu")
    object Game : Screen("game")
    object GameOver : Screen("game_over") {
        const val ARG_SCORE = "score"
        const val ARG_HIGH_SCORE = "highScore"
        
        fun createRoute(score: Int, highScore: Int): String {
            return "$route/$score/$highScore"
        }
    }
}
