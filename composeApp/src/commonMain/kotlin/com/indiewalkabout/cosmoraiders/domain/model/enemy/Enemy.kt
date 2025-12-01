package com.indiewalkabout.cosmoraiders.domain.model.enemy

import androidx.compose.animation.core.Animatable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class Enemy {
    var scope: CoroutineScope? = null
        protected set
    var x: Float = 0f
        protected set
    var y: Animatable<Float, *> = Animatable(0f)
        protected set
    var radius: Float = 0f
        protected set
    var fallingSpeed: Float = 0f
        protected set
    var scoreValue: Int = 0
        protected set
    var color: Color = Color(0xFFFFFFFF)
        protected set
    var lives: Int = 1
        protected set
    var isDestroyed: Boolean = false
        protected set

    // Public methods to update properties
    fun setEnemyScope(scope: CoroutineScope) {
        this.scope = scope
    }

    fun setPosition(x: Float, y: Animatable<Float, *>) {
        this.x = x
        this.y = y
    }

    fun setEnemyRadius(radius: Float) {
        this.radius = radius
    }

    fun setEnemyFallSpeed(speed: Float) {
        this.fallingSpeed = speed
    }

    fun setEnemyScoreValue(scoreValue: Int) {
        this.scoreValue = scoreValue
    }

    fun setEnemyColor(color: Color) {
        this.color = color
    }

    fun setEnemyLives(lives: Int) {
        this.lives = lives
    }

    fun destroy() {
        isDestroyed = true
        scope?.launch {
            y.stop()
        }
    }
}