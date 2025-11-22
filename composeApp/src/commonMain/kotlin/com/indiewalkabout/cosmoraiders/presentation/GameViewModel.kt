package com.indiewalkabout.cosmoraiders.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.indiewalkabout.cosmoraiders.domain.Game
import com.indiewalkabout.cosmoraiders.domain.GameAction
import com.indiewalkabout.cosmoraiders.domain.GameSettings
import com.indiewalkabout.cosmoraiders.domain.GameState
import com.indiewalkabout.cosmoraiders.domain.Weapon
import com.indiewalkabout.cosmoraiders.domain.audio.AudioPlayer
import com.indiewalkabout.cosmoraiders.domain.levels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// manages the game state and handles user interactions
class GameViewModel(
    private val audioPlayer: AudioPlayer
) {
    private val _gameState = MutableStateFlow<GameState>(GameState.Idle)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val game = Game()

    // Game objects
    var ninjaPosition by mutableStateOf(Offset.Zero)
    var weapon by mutableStateOf<Weapon?>(null)
    val targets = mutableListOf<Target>()

    // Screen dimensions
    var screenWidth by mutableStateOf(0.dp)
    var screenHeight by mutableStateOf(0.dp)

    // Game settings
    private var currentLevelSettings: GameSettings = GameSettings()

    init {
        // Initialize game state
        updateGameState()
    }

    // Update the game state on the current state
    private fun updateGameState() {
        _gameState.value = game.getCurrentState()
    }

    //  new game
    fun startGame() {
        game.processAction(GameAction.StartGame)
        updateGameState()
        resetLevel()
    }

    // Pause the game
    fun pauseGame() {
        game.processAction(GameAction.PauseGame)
        updateGameState()
    }

    // Resume game
    fun resumeGame() {
        game.processAction(GameAction.ResumeGame)
        updateGameState()
    }

    // End current game
    fun endGame() {
        game.processAction(GameAction.EndGame)
        updateGameState()
    }

    // Update the game state based on the frame time
    fun update(deltaTime: Float) {
        if (!game.isPlaying() || game.isPaused()) return

        // Update weapon position
        weapon?.let { currentWeapon ->
            val newY = currentWeapon.y - (currentLevelSettings.weaponSpeed * deltaTime)
            if (newY > 0) {
                weapon = currentWeapon.copy(y = newY)
            } else {
                weapon = null
            }
        }

        // Update targets
        targets.forEach { target ->
            // Update target position
            // Check for collisions
            // Handle target-specific behavior
        }

        // Check for level completion
        if (targets.isEmpty()) {
            completeLevel()
        }
    }

    // Fire a weapon from the current ninja position
    fun fireWeapon() {
        if (weapon != null || !game.isPlaying() || game.isPaused()) return

        weapon = Weapon(
            x = ninjaPosition.x,
            y = ninjaPosition.y,
            radius = 10f,
            shootingSpeed = currentLevelSettings.weaponSpeed
        )
        // TODO : add audio
        // audioPlayer.playShootSound()
    }

    // Reset current level
    private fun resetLevel() {
        // Clear existing targets
        targets.clear()

        // Reset weapon
        weapon = null

        // Get current level settings
        val level = game.getLevel()
        currentLevelSettings = levels.getOrElse(level - 1) { GameSettings() }

        // Initialize targets based on level
        // TODO: Implement target generation based on level
    }

    // Complete the current level and advance to the next one
    private fun completeLevel() {
        game.processAction(GameAction.CompleteLevel)
        updateGameState()
    }

    // Start the next level
    fun startNextLevel() {
        game.processAction(GameAction.StartNextLevel)
        updateGameState()
        resetLevel()
    }

    // Handle player losing a life
    private fun loseLife() {
        val currentLives = when (val state = game.getCurrentState()) {
            is GameState.Playing -> state.lives - 1
            else -> 0
        }

        game.processAction(GameAction.LoseLife(currentLives))
        updateGameState()

        if (currentLives > 0) {
            resetLevel()
        }
    }
}
