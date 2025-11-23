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
import com.indiewalkabout.cosmoraiders.domain.game.Game
import com.indiewalkabout.cosmoraiders.domain.game.GameState
import com.indiewalkabout.cosmoraiders.domain.game.GameStatus
import com.indiewalkabout.cosmoraiders.domain.MoveDirection
import com.indiewalkabout.cosmoraiders.domain.Bullet
import com.indiewalkabout.cosmoraiders.domain.audio.AudioPlayer
import com.indiewalkabout.cosmoraiders.domain.game.levels
import com.indiewalkabout.cosmoraiders.domain.enemy.EasyEnemy
import com.indiewalkabout.cosmoraiders.domain.enemy.MediumEnemy
import com.indiewalkabout.cosmoraiders.domain.enemy.StrongEnemy
import com.indiewalkabout.cosmoraiders.domain.enemy.Enemy
import com.indiewalkabout.cosmoraiders.util.detectMoveGesture
import com.stevdza_san.sprite.component.drawSpriteView
import com.stevdza_san.sprite.domain.SpriteFlip
import com.stevdza_san.sprite.domain.SpriteSheet
import com.stevdza_san.sprite.domain.SpriteSpec
import com.stevdza_san.sprite.domain.rememberSpriteState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import cosmoraiders.composeapp.generated.resources.Res
import cosmoraiders.composeapp.generated.resources.background
import cosmoraiders.composeapp.generated.resources.kunai
import cosmoraiders.composeapp.generated.resources.run_sprite
import cosmoraiders.composeapp.generated.resources.standing_ninja
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import kotlin.math.sqrt

// Game constants
const val PLAYER_FRAME_WIDTH  = 253
const val PLAYER_FRAME_HEIGHT = 303
const val WEAPON_SPAWN_RATE   = 150L
const val WEAPON_SIZE         = 32f
const val TARGET_SPAWN_RATE   = 1500L
const val TARGET_SIZE         = 40f

@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()
    val audio = koinInject<AudioPlayer>()
    
    // Game state management
    var game = remember { Game() }
    val stateManager = game.gameStateManager
    val currentState by stateManager.currentState.collectAsState()
    
    // Screen dimensions
    var screenWidth by remember { mutableStateOf(0) }
    var screenHeight by remember { mutableStateOf(0) }
    
    // Game objects
    val bullets = remember { mutableStateListOf<Bullet>() }
    val enemies = remember { mutableStateListOf<Enemy>() }
    var moveDirection by remember { mutableStateOf(MoveDirection.None) }

    // Handle game state changes
    LaunchedEffect(Unit) {
        stateManager.currentState.collectLatest { state ->
            when (state) {
                is GameState.Playing -> {
                    // Start game loop when game is playing
                    // (Game loop implementation will go here)
                }
                is GameState.GameOver -> {
                    // Handle game over
                    bullets.clear()
                    enemies.clear()
                }
                is GameState.LevelComplete -> {
                    // Handle level complete
                    bullets.clear()
                    enemies.clear()
                }
                else -> { /* Other states */ }
            }
        }
    }


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
                frameWidth = PLAYER_FRAME_WIDTH,
                frameHeight = PLAYER_FRAME_HEIGHT,
                image = Res.drawable.run_sprite
            )
        )
    }
    val standingPlayerSpec = remember {
        SpriteSpec(
            screenWidth = screenWidth.toFloat(),
            default = SpriteSheet(
                frameWidth = PLAYER_FRAME_WIDTH,
                frameHeight = PLAYER_FRAME_HEIGHT,
                image = Res.drawable.standing_ninja
            )
        )
    }
    val runningPlayerImage  = runningPlayerSpec.imageBitmap
    val standingPlayerImage = standingPlayerSpec.imageBitmap
    val playerBullet01Image = imageResource(Res.drawable.kunai)

    val ninjaOffsetX = remember(key1 = screenWidth) {
        Animatable(
            initialValue = ((screenWidth.toFloat()) / 2 - (PLAYER_FRAME_WIDTH / 2))
        )
    }

    // Spawn the Weapons
    LaunchedEffect(isRunning, currentState) {
        while (isRunning && currentState is GameState.Playing) {
            delay(WEAPON_SPAWN_RATE)
            bullets.add(
                Bullet(
                    x = ninjaOffsetX.value + (PLAYER_FRAME_WIDTH / 2),
                    y = screenHeight - PLAYER_FRAME_HEIGHT.toFloat() * 2,
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

    // Move Weapons & Targets and add Collision Detection
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

                // Check for collision
                val weaponIterator = bullets.iterator()
                while (weaponIterator.hasNext()) {
                    val weapon = weaponIterator.next()
                    val targetIterator = enemies.listIterator()
                    while (targetIterator.hasNext()) {
                        val target = targetIterator.next()
                        if (isCollision(weapon, target)) {
                            audio.playSound(index = 0)
                            if (target is StrongEnemy) {
                                if (target.lives > 0) {
                                    targetIterator.set(
                                        element = target.copy(
                                            radius = target.radius + 10,
                                            lives = target.lives - 1
                                        )
                                    )
                                    weaponIterator.remove()
                                } else {
                                    weaponIterator.remove()
                                    targetIterator.remove()
                                    game = game.copy(score = game.score + 5)
                                }
                            } else if (target is MediumEnemy) {
                                if (target.lives > 0) {
                                    targetIterator.set(
                                        element = target.copy(
                                            radius = target.radius + 10,
                                            lives = target.lives - 1
                                        )
                                    )
                                    weaponIterator.remove()
                                } else {
                                    weaponIterator.remove()
                                    targetIterator.remove()
                                    game = game.copy(score = game.score + 5)
                                }
                            } else if (target is EasyEnemy) {
                                weaponIterator.remove()
                                targetIterator.remove()
                                game = game.copy(score = game.score + 5)
                            }
                            break
                        }
                    }
                }

                // Check if Game Over
                val offScreenTarget = enemies.firstOrNull {
                    it.y.value > screenHeight
                }
                if(offScreenTarget != null) {
                    stateManager.gameOver()
                    runningPlayer.stop()
                    bullets.removeAll { true }
                    enemies.removeAll { true }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                screenWidth = it.size.width
                screenHeight = it.size.height
            }
            .pointerInput(currentState) {
                awaitPointerEventScope {
                    detectMoveGesture(
                        gameStatus = if (currentState is GameState.Playing) GameStatus.Started else GameStatus.Idle,
                        onLeft = {
                            moveDirection = MoveDirection.Left
                            runningPlayer.start()
                            scope.launch(Dispatchers.Main) {
                                while (isRunning) {
                                    ninjaOffsetX.animateTo(
                                        targetValue = if ((ninjaOffsetX.value - game.settings.playerSpeed) >= 0 - (PLAYER_FRAME_WIDTH / 2))
                                            ninjaOffsetX.value - game.settings.playerSpeed else ninjaOffsetX.value,
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
                                    ninjaOffsetX.animateTo(
                                        targetValue = if ((ninjaOffsetX.value + game.settings.playerSpeed + PLAYER_FRAME_WIDTH) <= screenWidth + (PLAYER_FRAME_WIDTH / 2))
                                            ninjaOffsetX.value + game.settings.playerSpeed else ninjaOffsetX.value,
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
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(Res.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw enemies
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
            // Draw bullets
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
                    x = ninjaOffsetX.value.toInt(),
                    y = (screenHeight - PLAYER_FRAME_HEIGHT - (PLAYER_FRAME_HEIGHT / 2))
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

if (currentState is GameState.MainMenu) {
        Column(
            modifier = Modifier
                .clickable(enabled = false) { }
                .background(Color.Black.copy(alpha = 0.7f))
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ready?",
                fontSize = MaterialTheme.typography.displayMedium.fontSize,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    stateManager.startNewGame()
                }
            ) {
                Text(text = "Start")
            }
        }
    }

if (currentState is GameState.GameOver) {
        Column(
            modifier = Modifier
                .clickable(enabled = false) { }
                .background(Color.Black.copy(alpha = 0.7f))
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Game Over!",
                fontSize = MaterialTheme.typography.displayLarge.fontSize,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Your Score: ${game.score}",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    stateManager.resetGame()
                    stateManager.startNewGame()
                }
            ) {
                Text(text = "Play again")
            }
        }
    }
}

fun isCollision(bullet: Bullet, enemy: Enemy): Boolean {
    val dx = bullet.x - enemy.x
    val dy = bullet.y - enemy.y.value
    val distance = sqrt(dx * dx + dy * dy)
    return distance < (bullet.radius + enemy.radius)
}