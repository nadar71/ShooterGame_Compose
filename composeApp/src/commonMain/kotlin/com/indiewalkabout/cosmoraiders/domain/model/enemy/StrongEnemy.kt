package com.indiewalkabout.cosmoraiders.domain.model.enemy

import androidx.compose.animation.core.Animatable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope

class StrongEnemy(
    scope: CoroutineScope,
    x: Float = 0f,
    y: Animatable<Float, *> = Animatable(0f),
    radius: Float = 50f,
    fallingSpeed: Float = 1f,
    color: Color = Color(0xFFFF6262),
    lives: Int = 3
) : Enemy() {
    init {
        this.scope = scope
        this.x = x
        this.y = y
        this.radius = radius
        this.fallingSpeed = fallingSpeed
        this.scoreValue = 300
        this.color = color
        this.lives = lives
    }
}
