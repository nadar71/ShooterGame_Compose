package com.indiewalkabout.cosmoraiders.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.indiewalkabout.cosmoraiders.GameOverScreen
import com.indiewalkabout.cosmoraiders.GameScreen
import com.indiewalkabout.cosmoraiders.MainMenuScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.MainMenu.route
    ) {
        composable(Screen.MainMenu.route) {
            MainMenuScreen(
                onStartGame = { navController.navigate(Screen.Game.route) }
            )
        }
        
        composable(Screen.Game.route) {
            GameScreen(
                onGameOver = { score, highScore ->
                    navController.navigate(Screen.GameOver.createRoute(score, highScore))
                },
                onExitToMenu = {
                    navController.navigate(Screen.MainMenu.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(
            route = "${Screen.GameOver.route}/{${Screen.GameOver.ARG_SCORE}}/{${Screen.GameOver.ARG_HIGH_SCORE}}",
            arguments = listOf(
                navArgument(Screen.GameOver.ARG_SCORE) { type = NavType.IntType },
                navArgument(Screen.GameOver.ARG_HIGH_SCORE) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt(Screen.GameOver.ARG_SCORE) ?: 0
            val highScore = backStackEntry.arguments?.getInt(Screen.GameOver.ARG_HIGH_SCORE) ?: 0
            
            GameOverScreen(
                finalScore = score,
                highScore = highScore,
                onPlayAgain = {
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                },
                onExitToMenu = {
                    navController.navigate(Screen.MainMenu.route) {
                        popUpTo(Screen.MainMenu.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
