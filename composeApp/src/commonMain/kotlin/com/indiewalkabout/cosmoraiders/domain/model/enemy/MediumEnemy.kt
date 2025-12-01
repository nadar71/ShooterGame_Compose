package com.indiewalkabout.cosmoraiders.domain.model.enemy

import androidx.compose.animation.core.Animatable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope

class MediumEnemy(
    scope: CoroutineScope,
    x: Float = 0f,
    y: Animatable<Float, *> = Animatable(0f),
    radius: Float = 40f,
    fallingSpeed: Float = 1.5f,
    color: Color = Color(0xFF7F52FF),
    lives: Int = 2
) : Enemy() {
    init {
        this.scope = scope
        this.x = x
        this.y = y
        this.radius = radius
        this.fallingSpeed = fallingSpeed
        this.scoreValue = 200
        this.color = color
        this.lives = lives
    }
}
