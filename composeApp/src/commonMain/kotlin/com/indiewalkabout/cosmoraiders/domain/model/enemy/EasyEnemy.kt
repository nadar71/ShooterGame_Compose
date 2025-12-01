package com.indiewalkabout.cosmoraiders.domain.model.enemy

import androidx.compose.animation.core.Animatable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope

class EasyEnemy(
    scope: CoroutineScope,
    x: Float = 0f,
    y: Animatable<Float, *> = Animatable(0f),
    radius: Float = 30f,
    fallingSpeed: Float = 2f,
    color: Color = Color(0xFFFFFFFF)
) : Enemy() {
    init {
        this.scope = scope
        this.x = x
        this.y = y
        this.radius = radius
        this.fallingSpeed = fallingSpeed
        this.scoreValue = 100
        this.color = color
    }
}
