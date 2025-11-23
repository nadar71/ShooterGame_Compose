package com.indiewalkabout.cosmoraiders.domain.enemy

import androidx.compose.animation.core.Animatable
import androidx.compose.ui.graphics.Color

interface Enemy {
    val x: Float
    val y: Animatable<Float, *>
    val radius: Float
    val fallingSpeed: Float
    val color: Color
}