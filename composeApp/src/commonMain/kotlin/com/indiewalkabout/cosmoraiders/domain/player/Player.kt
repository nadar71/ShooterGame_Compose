package com.indiewalkabout.cosmoraiders.domain.player

import androidx.compose.ui.geometry.Offset
import com.indiewalkabout.cosmoraiders.PLAYER_LIVES

class Player {
    // Player state
    var lives: Int = PLAYER_LIVES
        private set
    
    var isLifeLost: Boolean = false
        internal set
    
    var position: Offset = Offset.Zero
        private set
    
    // Player constants
    companion object {
        // Sprite dimensions
        const val FRAME_WIDTH = 253
        const val FRAME_HEIGHT = 303
        
        // Movement
        const val DEFAULT_SPEED = 10
        
        // Collision
        const val COLLISION_RADIUS_FACTOR = 0.6f // Slightly smaller than actual size for better gameplay
        
        // Animation
        const val TOTAL_FRAMES = 9
        const val FRAMES_PER_ROW = 3
        const val ANIMATION_SPEED_MS = 100L

        // create a new player instance with default values
        fun create(): Player = Player().apply {
            reset()
        }
    }
    
    // Computed properties
    val collisionRadius: Float
        get() = (FRAME_WIDTH / 2) * COLLISION_RADIUS_FACTOR
    
    val centerX: Float
        get() = position.x + (FRAME_WIDTH / 2)
    
    val centerY: Float
        get() = position.y + (FRAME_HEIGHT / 2)
    
    val bottom: Float
        get() = position.y + FRAME_HEIGHT
    
    // Player actions
    fun decreaseLives(amount: Int = 1) {
        lives = (lives - amount).coerceAtLeast(0)
        isLifeLost = true
        println("Player: Lives decreased to $lives")
    }
    
    fun updatePosition(x: Float, y: Float) {
        position = Offset(x, y)
    }
    
    fun reset() {
        lives = PLAYER_LIVES
        isLifeLost = false
        position = Offset.Zero
        println("Player: Reset to initial state")
    }
    
    fun markLifeLostProcessed() {
        isLifeLost = false
    }

}
