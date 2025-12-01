package com.indiewalkabout.cosmoraiders.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.indiewalkabout.cosmoraiders.presentation.ui.GameOverScreen
import com.indiewalkabout.cosmoraiders.presentation.ui.GameScreen
import com.indiewalkabout.cosmoraiders.presentation.ui.MainMenuScreen
import com.indiewalkabout.cosmoraiders.presentation.state.GameStateManager
import org.koin.compose.koinInject

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val stateManager = koinInject<GameStateManager>()
    
    NavHost(
        navController = navController,
        startDestination = Screen.MainMenu.route
    ) {
        composable(Screen.MainMenu.route) {
            MainMenuScreen(
                onStartGame = {
                    println("MainMenu: Start btn pressed, starting game...")
                    stateManager.startNewGame()
                    navController.navigate(Screen.Game.route)
                }
            )
        }
        
        composable(Screen.Game.route) {
            GameScreen(
                onGameOver = { score, highScore ->
                    println("GameScreen: game over action")
                    navController.navigate(Screen.GameOver.createRoute(score, highScore))
                },
                onExitToMenu = {
                    println("GameScreen: Exit to menu btn pressed, exiting to menu...")
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
                    println("GameOverScreen: Play again btn pressed, playing again...")
                    stateManager.startNewGame()
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                },
                onExitToMenu = {
                    println("GameOverScreen: Exit to menu btn pressed, exiting to menu...")
                    stateManager.showMainMenu()
                    navController.navigate(Screen.MainMenu.route) {
                        popUpTo(Screen.MainMenu.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
