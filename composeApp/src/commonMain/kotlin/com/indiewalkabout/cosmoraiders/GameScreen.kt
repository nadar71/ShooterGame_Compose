package com.indiewalkabout.cosmoraiders

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.indiewalkabout.cosmoraiders.domain.Bullet
import com.indiewalkabout.cosmoraiders.domain.MoveDirection
import com.indiewalkabout.cosmoraiders.domain.audio.AudioPlayer
import com.indiewalkabout.cosmoraiders.domain.checkEnemyCollisions
import com.indiewalkabout.cosmoraiders.domain.enemy.EasyEnemy
import com.indiewalkabout.cosmoraiders.domain.enemy.Enemy
import com.indiewalkabout.cosmoraiders.domain.enemy.MediumEnemy
import com.indiewalkabout.cosmoraiders.domain.enemy.StrongEnemy
import com.indiewalkabout.cosmoraiders.domain.game.Game
import com.indiewalkabout.cosmoraiders.domain.game.GameState
import com.indiewalkabout.cosmoraiders.domain.game.levels
import com.indiewalkabout.cosmoraiders.util.detectMoveGesture
import com.stevdza_san.sprite.component.drawSpriteView
import com.stevdza_san.sprite.domain.SpriteFlip
import com.stevdza_san.sprite.domain.SpriteSheet
import com.stevdza_san.sprite.domain.SpriteSpec
import com.stevdza_san.sprite.domain.rememberSpriteState
import cosmoraiders.composeapp.generated.resources.Res
import cosmoraiders.composeapp.generated.resources.background
import cosmoraiders.composeapp.generated.resources.kunai
import cosmoraiders.composeapp.generated.resources.run_sprite
import cosmoraiders.composeapp.generated.resources.standing_ninja
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

import com.indiewalkabout.cosmoraiders.domain.player.Player

// Game constants
const val PLAYER_LIVES = 3
const val WEAPON_SPAWN_RATE = 150L
const val WEAPON_SIZE = 32f
const val TARGET_SPAWN_RATE = 1500L
const val TARGET_SIZE = 40f

@Composable
fun GameScreen(
    onGameOver: (score: Int, highScore: Int) -> Unit,
    onExitToMenu: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val audio = koinInject<AudioPlayer>()

    // Game state instance and management
    var game = remember { Game() }
    val stateManager = game.gameStateManager

    // Collect the game state once
    val currentState by stateManager.currentState.collectAsStateWithLifecycle()

    // Track game objects and UI state
    val bullets = remember { mutableStateListOf<Bullet>() }
    val enemies = remember { mutableStateListOf<Enemy>() }

    // Debug the current state
    LaunchedEffect(currentState) {
        println("Current game state changed to: $currentState")
    }
    var moveDirection by remember { mutableStateOf(MoveDirection.None) }
    var screenWidth by remember { mutableStateOf(0) }
    var screenHeight by remember { mutableStateOf(0) }

    // --- player stuff ---
    val runningPlayer = rememberSpriteState(
        totalFrames = 9,
        framesPerRow = 3,
        animationSpeed = 100L
    )
    val standingPlayer = rememberSpriteState(
        totalFrames = 1,
        framesPerRow = 1,
        animationSpeed = 100L
    )

    val currentRunningFrame by runningPlayer.currentFrame.collectAsState()
    val currentStandingFrame by standingPlayer.currentFrame.collectAsState()
    val isRunning by runningPlayer.isRunning.collectAsState()
    val runningPlayerSpec = remember {
        SpriteSpec(
            screenWidth = screenWidth.toFloat(),
            default = SpriteSheet(
                frameWidth = Player.FRAME_WIDTH,
                frameHeight = Player.FRAME_HEIGHT,
                image = Res.drawable.run_sprite
            )
        )
    }
    val standingPlayerSpec = remember {
        SpriteSpec(
            screenWidth = screenWidth.toFloat(),
            default = SpriteSheet(
                frameWidth = Player.FRAME_WIDTH,
                frameHeight = Player.FRAME_HEIGHT,
                image = Res.drawable.standing_ninja
            )
        )
    }
    val runningPlayerImage = runningPlayerSpec.imageBitmap
    val standingPlayerImage = standingPlayerSpec.imageBitmap
    val playerBullet01Image = imageResource(Res.drawable.kunai)

    val playerOffsetX = remember(key1 = screenWidth) {
        Animatable(
            initialValue = ((screenWidth.toFloat()) / 2 - (Player.FRAME_WIDTH / 2))
        )
    }

    // Handle game state changes
    LaunchedEffect(currentState) {
        when (val state = currentState) {
            is GameState.GameOver -> {
                println(
                    "Game Over state detected in UI. Score: ${state.finalScore}, " +
                            "High Score: ${state.highScore}"
                )
                runningPlayer.stop()
                bullets.clear()
                enemies.clear()
                // Notify parent about game over with scores
                onGameOver(state.finalScore, state.highScore)
            }

            is GameState.Playing -> {
                println("Game playing state detected. Resetting game objects if needed.")
                // Reset game objects when starting a new game
                if (bullets.isNotEmpty() || enemies.isNotEmpty()) {
                    bullets.clear()
                    enemies.clear()
                    game.player.reset()
                    playerOffsetX.snapTo((screenWidth.toFloat() / 2) - (Player.FRAME_WIDTH / 2))
                }
            }

            is GameState.MainMenu -> {
                // Handle any cleanup needed when returning to main menu
                runningPlayer.stop()
                bullets.clear()
                enemies.clear()
                onExitToMenu()
            }

            else -> {
                println("Other state detected: $state")
            }
        }
    }

    // sound at life loosing
    LaunchedEffect(game.player.isLifeLost) {
        if (game.player.isLifeLost && game.player.lives > 0) {
            audio.playSound(0)
            game.player.markLifeLostProcessed()
        }
    }

    // Spawn the weapons
    LaunchedEffect(isRunning, currentState) {
        while (isRunning && currentState is GameState.Playing) {
            delay(WEAPON_SPAWN_RATE)
            bullets.add(
                Bullet(
                    x = playerOffsetX.value + (Player.FRAME_WIDTH / 2),
                    y = screenHeight - Player.FRAME_HEIGHT.toFloat() * 2,
                    radius = WEAPON_SIZE,
                    shootingSpeed = -game.settings.weaponSpeed
                )
            )
        }
    }

    // Spawn the enemies
    LaunchedEffect(currentState) {
        while (currentState is GameState.Playing) {
            delay(TARGET_SPAWN_RATE)
            val randomX = (0..screenWidth).random()
            val isEven = (randomX % 2 == 0)
            if (isEven) {
                enemies.add(
                    MediumEnemy(
                        x = randomX.toFloat(),
                        y = Animatable(0f),
                        radius = TARGET_SIZE,
                        fallingSpeed = game.settings.targetSpeed
                    )
                )
            } else if (randomX > screenWidth * 0.75) {
                enemies.add(
                    StrongEnemy(
                        x = randomX.toFloat(),
                        y = Animatable(0f),
                        radius = TARGET_SIZE,
                        fallingSpeed = game.settings.targetSpeed * 0.25f
                    )
                )
            } else {
                enemies.add(
                    EasyEnemy(
                        x = randomX.toFloat(),
                        y = Animatable(0f),
                        radius = TARGET_SIZE,
                        fallingSpeed = game.settings.targetSpeed
                    )
                )
            }
        }
    }

    // Move Weapons,Enemies and add Collision Detection
    LaunchedEffect(currentState) {
        while (currentState is GameState.Playing) {
            withFrameMillis {
                enemies.forEach { target ->
                    scope.launch(Dispatchers.Main) {
                        target.y.animateTo(
                            targetValue = target.y.value + target.fallingSpeed
                        )
                    }
                }
                bullets.forEach { weapon ->
                    weapon.y += weapon.shootingSpeed
                }

                // Check for collisions
                checkEnemyCollisions(
                    bullets = bullets,
                    enemies = enemies,
                    onCollision = { _, points ->
                        game = game.copy(score = game.score + points)
                    },
                    onSoundPlay = { index -> audio.playSound(index) }
                )

                // Update player position
                game.player.updatePosition(
                    x = playerOffsetX.value,
                    y = (screenHeight - Player.FRAME_HEIGHT).toFloat()
                )

                // Use player's calculated properties
                val playerCenterX = game.player.centerX
                val playerCenterY = game.player.centerY
                val playerRadius = game.player.collisionRadius

                // Check if Game Over
                stateManager.checkGameOver(
                    game = game,
                    enemies = enemies,
                    playerX = playerCenterX,
                    playerY = playerCenterY,
                    playerRadius = playerRadius,
                    screenHeight = screenHeight,
                    onGameOver = {
                        println("Game Over - Score: ${game.score}")
                        runningPlayer.stop()
                        bullets.clear()
                        enemies.clear()
                    },
                )
            }
        }
    }

    // ------------------------------------------ UI -----------------------------------------------
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                screenWidth = it.size.width
                screenHeight = it.size.height
            }
            .pointerInput(currentState) {
                awaitPointerEventScope {
                    // Handle touch input and move player
                    detectMoveGesture(
                        gameState = currentState,
                        onLeft = {
                            moveDirection = MoveDirection.Left
                            runningPlayer.start()
                            scope.launch(Dispatchers.Main) {
                                while (isRunning) {
                                    playerOffsetX.animateTo(
                                        targetValue =
                                            if ((playerOffsetX.value - game.settings.playerSpeed) >= 0
                                                - (Player.FRAME_WIDTH.toFloat() / 2)
                                            )
                                                playerOffsetX.value - game.settings.playerSpeed
                                            else playerOffsetX.value,
                                        animationSpec = tween(30)
                                    )
                                }
                            }
                        },
                        onRight = {
                            moveDirection = MoveDirection.Right
                            runningPlayer.start()
                            scope.launch(Dispatchers.Main) {
                                while (isRunning) {
                                    playerOffsetX.animateTo(
                                        targetValue =
                                            if ((playerOffsetX.value + game.settings.playerSpeed + Player.FRAME_WIDTH)
                                                <= screenWidth + (Player.FRAME_WIDTH / 2)
                                            )
                                                playerOffsetX.value + game.settings.playerSpeed
                                            else playerOffsetX.value,
                                        animationSpec = tween(30)
                                    )
                                }
                            }
                        },
                        onFingerLifted = {
                            moveDirection = MoveDirection.None
                            runningPlayer.stop()
                        }
                    )
                }
            }
    ) {

        // --- Background ---
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(Res.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        // --- Game Scene ---
        Canvas(modifier = Modifier.fillMaxSize()) {

            // -- Draw enemies
            enemies.forEach { target ->
                drawCircle(
                    color = target.color,
                    radius = target.radius,
                    center = Offset(
                        x = target.x,
                        y = target.y.value
                    )
                )
            }

            // -- Draw player bullets
            bullets.forEach { weapon ->
                drawImage(
                    image = playerBullet01Image,
                    dstOffset = IntOffset(
                        x = weapon.x.toInt(),
                        y = weapon.y.toInt()
                    )
                )
            }
            drawSpriteView(
                spriteState = if (isRunning) runningPlayer else standingPlayer,
                spriteSpec = if (isRunning) runningPlayerSpec else standingPlayerSpec,
                currentFrame = if (isRunning) currentRunningFrame else currentStandingFrame,
                image = if (isRunning) runningPlayerImage else standingPlayerImage,
                spriteFlip = if (moveDirection == MoveDirection.Left)
                    SpriteFlip.Horizontal else null,
                offset = IntOffset(
                    x = playerOffsetX.value.toInt(),
                    y = (screenHeight - Player.FRAME_HEIGHT - (Player.FRAME_HEIGHT / 2))
                )
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 34.dp,
                vertical = 34.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Level: ${levels.firstOrNull { it.first.score >= game.score }?.first?.name ?: "MAX"}",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
        )
        Text(
            text = "Score: ${game.score}",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
        )
    }

    // Handle MainMenu state - navigation is handled by the NavController
    LaunchedEffect(currentState) {
        when (val state = currentState) {
            is GameState.MainMenu -> {
                // Clean up and navigate to main menu
                runningPlayer.stop()
                bullets.clear()
                enemies.clear()
                onExitToMenu()
            }
            is GameState.GameOver -> {
                // Clean up and navigate to game over screen
                runningPlayer.stop()
                bullets.clear()
                enemies.clear()
                onGameOver(state.finalScore, state.highScore)
            }
            is GameState.Playing -> {
                // Reset game objects when starting a new game
                if (bullets.isNotEmpty() || enemies.isNotEmpty()) {
                    bullets.clear()
                    enemies.clear()
                    game.player.reset()
                    playerOffsetX.snapTo((screenWidth.toFloat() / 2) - (Player.FRAME_WIDTH / 2))
                }
            }
            else -> {}
        }
        // The game over and main menu UIs are now handled by their respective screens
    }
}

