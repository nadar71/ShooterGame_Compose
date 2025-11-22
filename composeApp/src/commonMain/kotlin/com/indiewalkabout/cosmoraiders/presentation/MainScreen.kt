package com.indiewalkabout.cosmoraiders.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indiewalkabout.cosmoraiders.domain.Game
import com.indiewalkabout.cosmoraiders.domain.GameSettings
import com.indiewalkabout.cosmoraiders.domain.GameState
import com.indiewalkabout.cosmoraiders.domain.MoveDirection
import com.indiewalkabout.cosmoraiders.domain.Weapon
import com.indiewalkabout.cosmoraiders.domain.audio.AudioPlayer
import com.indiewalkabout.cosmoraiders.domain.levels
import com.indiewalkabout.cosmoraiders.domain.target.EasyTarget
import com.indiewalkabout.cosmoraiders.domain.target.MediumTarget
import com.indiewalkabout.cosmoraiders.domain.target.StrongTarget
import com.indiewalkabout.cosmoraiders.domain.target.Target
import com.indiewalkabout.cosmoraiders.util.detectMoveGesture
import com.stevdza_san.sprite.component.drawSpriteView
import com.stevdza_san.sprite.domain.SpriteFlip
import com.stevdza_san.sprite.domain.SpriteSheet
import com.stevdza_san.sprite.domain.SpriteSpec
import com.stevdza_san.sprite.domain.rememberSpriteState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

const val NINJA_FRAME_WIDTH = 253
const val NINJA_FRAME_HEIGHT = 303
const val WEAPON_SPAWN_RATE = 150L
const val WEAPON_SIZE = 32f
const val TARGET_SPAWN_RATE = 1500L
const val TARGET_SIZE = 40f

/*@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()
    val audio = koinInject<AudioPlayer>()
    var game by remember { mutableStateOf(Game()) }
    val weapons = remember { mutableStateListOf<Weapon>() }
    val targets = remember { mutableStateListOf<Target>() }
    var moveDirection by remember { mutableStateOf(MoveDirection.None) }
    var screenWidth by remember { mutableStateOf(0) }
    var screenHeight by remember { mutableStateOf(0) }

    // Update difficulty levels
    LaunchedEffect(game.score) {
        levels
            .filter { it.first.score == game.score }
            .takeIf { it.isNotEmpty() }
            ?.forEach { (_, nextLevel) ->
                game = game.copy(
                    settings = GameSettings(
                        ninjaSpeed = game.settings.ninjaSpeed + nextLevel.ninjaSpeed,
                        weaponSpeed = game.settings.weaponSpeed + nextLevel.weaponSpeed,
                        targetSpeed = game.settings.targetSpeed + nextLevel.targetSpeed,
                    )
                )
            }
    }

    val runningSprite = rememberSpriteState(
        totalFrames = 9,
        framesPerRow = 3
    )
    val standingSprite = rememberSpriteState(
        totalFrames = 1,
        framesPerRow = 1
    )
    val currentRunningFrame by runningSprite.currentFrame.collectAsState()
    val currentStandingFrame by standingSprite.currentFrame.collectAsState()
    val isRunning by runningSprite.isRunning.collectAsState()
    val runningSpriteSpec = remember {
        SpriteSpec(
            screenWidth = screenWidth.toFloat(),
            default = SpriteSheet(
                frameWidth = NINJA_FRAME_WIDTH,
                frameHeight = NINJA_FRAME_HEIGHT,
                image = Res.drawable.run_sprite
            )
        )
    }
    val standingSpriteSpec = remember {
        SpriteSpec(
            screenWidth = screenWidth.toFloat(),
            default = SpriteSheet(
                frameWidth = NINJA_FRAME_WIDTH,
                frameHeight = NINJA_FRAME_HEIGHT,
                image = Res.drawable.standing_ninja
            )
        )
    }
    val runningImage = runningSpriteSpec.imageBitmap
    val standingImage = standingSpriteSpec.imageBitmap
    val kunaiImage = imageResource(Res.drawable.kunai)

    val ninjaOffsetX = remember(key1 = screenWidth) {
        Animatable(
            initialValue = ((screenWidth.toFloat()) / 2 - (NINJA_FRAME_WIDTH / 2))
        )
    }

    // Spawn the Weapons
    LaunchedEffect(isRunning, game.status) {
        while (isRunning && game.status == GameStatus.Started) {
            delay(WEAPON_SPAWN_RATE)
            weapons.add(
                Weapon(
                    x = ninjaOffsetX.value + (NINJA_FRAME_WIDTH / 2),
                    y = screenHeight - NINJA_FRAME_HEIGHT.toFloat() * 2,
                    radius = WEAPON_SIZE,
                    shootingSpeed = -game.settings.weaponSpeed
                )
            )
        }
    }

    // Spawn the Targets
    LaunchedEffect(game.status) {
        while (game.status == GameStatus.Started) {
            delay(TARGET_SPAWN_RATE)
            val randomX = (0..screenWidth).random()
            val isEven = (randomX % 2 == 0)
            if (isEven) {
                targets.add(
                    MediumTarget(
                        x = randomX.toFloat(),
                        y = Animatable(0f),
                        radius = TARGET_SIZE,
                        fallingSpeed = game.settings.targetSpeed
                    )
                )
            } else if (randomX > screenWidth * 0.75) {
                targets.add(
                    StrongTarget(
                        x = randomX.toFloat(),
                        y = Animatable(0f),
                        radius = TARGET_SIZE,
                        fallingSpeed = game.settings.targetSpeed * 0.25f
                    )
                )
            } else {
                targets.add(
                    EasyTarget(
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
    LaunchedEffect(game.status) {
        while (game.status == GameStatus.Started) {
            withFrameMillis {
                targets.forEach { target ->
                    scope.launch(Dispatchers.Main) {
                        target.y.animateTo(
                            targetValue = target.y.value + target.fallingSpeed
                        )
                    }
                }
                weapons.forEach { weapon ->
                    weapon.y += weapon.shootingSpeed
                }

                // Check for collision
                val weaponIterator = weapons.iterator()
                while (weaponIterator.hasNext()) {
                    val weapon = weaponIterator.next()
                    val targetIterator = targets.listIterator()
                    while (targetIterator.hasNext()) {
                        val target = targetIterator.next()
                        if (isCollision(weapon, target)) {
                            audio.playSound(index = 0)
                            if (target is StrongTarget) {
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
                            } else if (target is MediumTarget) {
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
                            } else if (target is EasyTarget) {
                                weaponIterator.remove()
                                targetIterator.remove()
                                game = game.copy(score = game.score + 5)
                            }
                            break
                        }
                    }
                }

                // Check if Game Over
                val offScreenTarget = targets.firstOrNull {
                    it.y.value > screenHeight
                }
                if(offScreenTarget != null) {
                    game = game.copy(
                        status = GameStatus.Over
                    )
                    runningSprite.stop()
                    weapons.removeAll { true }
                    targets.removeAll { true }
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
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    detectMoveGesture(
                        gameStatus = game.status,
                        onLeft = {
                            moveDirection = MoveDirection.Left
                            runningSprite.start()
                            scope.launch(Dispatchers.Main) {
                                while (isRunning) {
                                    ninjaOffsetX.animateTo(
                                        targetValue = if ((ninjaOffsetX.value - game.settings.ninjaSpeed) >= 0 - (NINJA_FRAME_WIDTH / 2))
                                            ninjaOffsetX.value - game.settings.ninjaSpeed else ninjaOffsetX.value,
                                        animationSpec = tween(30)
                                    )
                                }
                            }
                        },
                        onRight = {
                            moveDirection = MoveDirection.Right
                            runningSprite.start()
                            scope.launch(Dispatchers.Main) {
                                while (isRunning) {
                                    ninjaOffsetX.animateTo(
                                        targetValue = if ((ninjaOffsetX.value + game.settings.ninjaSpeed + NINJA_FRAME_WIDTH) <= screenWidth + (NINJA_FRAME_WIDTH / 2))
                                            ninjaOffsetX.value + game.settings.ninjaSpeed else ninjaOffsetX.value,
                                        animationSpec = tween(30)
                                    )
                                }
                            }
                        },
                        onFingerLifted = {
                            moveDirection = MoveDirection.None
                            runningSprite.stop()
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
            targets.forEach { target ->
                drawCircle(
                    color = target.color,
                    radius = target.radius,
                    center = Offset(
                        x = target.x,
                        y = target.y.value
                    )
                )
            }
            weapons.forEach { weapon ->
                drawImage(
                    image = kunaiImage,
                    dstOffset = IntOffset(
                        x = weapon.x.toInt(),
                        y = weapon.y.toInt()
                    )
                )
            }
            drawSpriteView(
                spriteState = if (isRunning) runningSprite else standingSprite,
                spriteSpec = if (isRunning) runningSpriteSpec else standingSpriteSpec,
                currentFrame = if (isRunning) currentRunningFrame else currentStandingFrame,
                image = if (isRunning) runningImage else standingImage,
                spriteFlip = if (moveDirection == MoveDirection.Left)
                    SpriteFlip.Horizontal else null,
                offset = IntOffset(
                    x = ninjaOffsetX.value.toInt(),
                    y = (screenHeight - NINJA_FRAME_HEIGHT - (NINJA_FRAME_HEIGHT / 2))
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

    if (game.status == GameStatus.Idle) {
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
                    game = game.copy(status = GameStatus.Started)
                }
            ) {
                Text(text = "Start")
            }
        }
    }

    if (game.status == GameStatus.Over) {
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
                    game = game.copy(
                        score = 0,
                        status = GameStatus.Started,
                        settings = GameSettings()
                    )
                }
            ) {
                Text(text = "Play again")
            }
        }
    }
}*/

@Composable
fun MainScreen(
    viewModel: GameViewModel = remember { GameViewModel(AudioPlayer()) }
) {
    val gameState by viewModel.gameState.collectAsState()
    val scope = rememberCoroutineScope()

    // Handle game state changes
    LaunchedEffect(gameState) {
        when (gameState) {
            is GameState.Idle -> { /* Show main menu */ }
            is GameState.Playing -> {
                if ((gameState as GameState.Playing).isPaused) {
                    // Handle pause
                } else {
                    // Resume game
                }
            }
            is GameState.LevelComplete -> { /* Show level complete screen */ }
            is GameState.GameOver -> { /* Show game over screen */ }
        }
    }

    // Game loop
    val frameTimeNanos = remember { System.nanoTime() }
    var lastFrameTime by remember { mutableStateOf(frameTimeNanos) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { frameTime ->
                val deltaTime = (frameTime - lastFrameTime) / 1_000_000_000f
                lastFrameTime = frameTime
                if (gameState is GameState.Playing && !(gameState as GameState.Playing).isPaused) {
                    viewModel.update(deltaTime)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    viewModel.fireWeapon()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Game canvas
        GameCanvas(viewModel = viewModel)

        // UI Overlay
        when (val state = gameState) {
            is GameState.Idle -> MainMenu(
                onStartClick = { viewModel.startGame() }
            )
            is GameState.Playing -> {
                if (state.isPaused) {
                    PauseMenu(
                        onResume = { viewModel.resumeGame() },
                        onQuit = { viewModel.endGame() }
                    )
                } else {
                    GameHUD(
                        score = state.score,
                        level = state.level,
                        lives = state.lives,
                        onPause = { viewModel.pauseGame() }
                    )
                }
            }
            is GameState.LevelComplete -> LevelCompleteScreen(
                level = state.level,
                score = state.score,
                onNextLevel = { viewModel.startNextLevel() }
            )
            is GameState.GameOver -> GameOverScreen(
                score = state.finalScore,
                highScore = state.highScore,
                levelReached = state.levelReached,
                onRestart = { viewModel.startGame() }
            )
        }
    }
}

@Composable
private fun GameCanvas(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                viewModel.screenWidth = with(LocalDensity.current) { coordinates.size.width.toDp() }
                viewModel.screenHeight = with(LocalDensity.current) { coordinates.size.height.toDp() }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    viewModel.ninjaPosition = change.position
                }
            }
    ) {
        // Draw ninja (player)
        drawCircle(
            color = Color.Blue,
            radius = 30f,
            center = viewModel.ninjaPosition
        )

        // Draw weapon if exists
        viewModel.weapon?.let { weapon ->
            drawCircle(
                color = Color.Red,
                radius = weapon.radius,
                center = Offset(weapon.x, weapon.y)
            )
        }

        // Draw targets
        viewModel.targets.forEach { target ->
            drawCircle(
                color = target.color,
                radius = target.radius,
                center = Offset(target.x, target.y.value)
            )
        }
    }
}

@Composable
private fun MainMenu(
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "NINJA BUBBLE",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onStartClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("START GAME")
        }
    }
}

@Composable
private fun GameHUD(
    score: Int,
    level: Int,
    lives: Int,
    onPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("SCORE: $score", color = Color.White)
            Text("LEVEL: $level", color = Color.White)
            Text("LIVES: $lives", color = Color.White)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onPause,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("PAUSE")
        }
    }
}

@Composable
private fun PauseMenu(
    onResume: () -> Unit,
    onQuit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "PAUSED",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onResume) {
            Text("RESUME")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onQuit) {
            Text("QUIT GAME")
        }
    }
}

@Composable
private fun LevelCompleteScreen(
    level: Int,
    score: Int,
    onNextLevel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "LEVEL $level COMPLETE!",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Green
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Score: $score", color = Color.White)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNextLevel) {
            Text("NEXT LEVEL")
        }
    }
}

@Composable
private fun GameOverScreen(
    score: Int,
    highScore: Int?,
    levelReached: Int,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "GAME OVER",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Red,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("Final Score: $score", color = Color.White, fontSize = 24.sp)
        highScore?.let {
            Text("High Score: $it", color = Color.Yellow, fontSize = 20.sp)
        }
        Text("Level Reached: $levelReached", color = Color.White, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text("PLAY AGAIN", style = MaterialTheme.typography.titleMedium)
        }
    }
}

fun isCollision(weapon: Weapon, target: Target): Boolean {
    val dx = weapon.x - target.x
    val dy = weapon.y - target.y.value
    val distance = sqrt(dx * dx + dy * dy)
    return distance < (weapon.radius + target.radius)
}