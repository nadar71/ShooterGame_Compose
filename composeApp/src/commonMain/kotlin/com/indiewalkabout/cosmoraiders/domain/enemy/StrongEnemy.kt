package com.indiewalkabout.cosmoraiders.domain.enemy

import androidx.compose.animation.core.Animatable
import androidx.compose.ui.graphics.Color

data class StrongEnemy(
    override val x: Float = 0f,
    override val y: Animatable<Float, *> = Animatable(0f),
    override val radius: Float = 0f,
    override val fallingSpeed: Float = 0f,
    override val color: Color = Color(0xFFFF6262),
    val lives: Int = 3
): Enemy
