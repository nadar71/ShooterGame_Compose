package com.indiewalkabout.cosmoraiders.domain

import com.indiewalkabout.cosmoraiders.domain.enemy.Enemy
import com.indiewalkabout.cosmoraiders.domain.enemy.StrongEnemy
import com.indiewalkabout.cosmoraiders.domain.enemy.MediumEnemy
import com.indiewalkabout.cosmoraiders.domain.enemy.EasyEnemy
import com.indiewalkabout.cosmoraiders.domain.player.Player
import kotlin.math.sqrt
import kotlin.text.Typography.bullet

fun isCollisionBulletEnemy(bullet: Bullet, enemy: Enemy): Boolean {
    val dx = bullet.x - enemy.x
    val dy = bullet.y - enemy.y.value
    val distance = kotlin.math.hypot(dx, dy).toDouble()
    return distance < (bullet.radius + enemy.radius)
}

fun isCollisionPlayerEnemy(player: Player, enemy: Enemy): Boolean {
    val dx = player.centerX - enemy.x
    val dy = player.centerY - enemy.y.value
    val distance = kotlin.math.hypot(dx, dy).toDouble()
    return distance < (player.radius + enemy.radius)
}

fun checkEnemyCollisions(
    player: Player,
    bullets: MutableList<Bullet>,
    enemies: MutableList<Enemy>,
    onCollision: (enemy: Enemy, points: Int) -> Unit,
    onSoundPlay: (index: Int) -> Unit
) {
    val enemyIterator = enemies.listIterator()
    while (enemyIterator.hasNext()) {
        val bulletIterator = bullets.iterator()
        val enemy = enemyIterator.next()
        while (bulletIterator.hasNext()) {
            val bullet = bulletIterator.next()
            // check player's bullet-enemy collision
            if (isCollisionBulletEnemy(bullet, enemy)) {
                println("Bullet hit enemy: $enemy")
                onSoundPlay(0)
                when (enemy) {
                    is StrongEnemy -> handleStrongEnemyCollision(enemy, enemyIterator, bulletIterator)
                    is MediumEnemy -> handleMediumEnemyCollision(enemy, enemyIterator, bulletIterator)
                    is EasyEnemy   -> handleEasyEnemyCollision(enemy, enemyIterator, bulletIterator, 5)
                }
                onCollision(enemy, 5)
                break
            }
        }

        // check player-enemy collision
        if (isCollisionPlayerEnemy(player, enemy)) {
            println("Enemy hit player:")
            onSoundPlay(0)
            when (enemy) {
                is StrongEnemy -> handleStrongEnemyCollision(enemy, enemyIterator, bulletIterator)
                is MediumEnemy -> handleMediumEnemyCollision(enemy, enemyIterator, bulletIterator)
                is EasyEnemy   -> handleEasyEnemyCollision(enemy, enemyIterator, bulletIterator, 5)
            }
            onCollision(enemy, 5)
            player.switchHitFlag()
            break
        }
    }
}

// TODO : set these inside enemy class
private fun handleStrongEnemyCollision(
    enemy: StrongEnemy,
    enemyIterator: MutableListIterator<Enemy>,
    bulletIterator: MutableIterator<Bullet>
) {
    if (enemy.lives > 0) {
        enemyIterator.set(
            enemy.copy(
                radius = enemy.radius + 10,
                lives = enemy.lives - 1
            )
        )
        bulletIterator.remove()
    } else {
        bulletIterator.remove()
        enemyIterator.remove()
    }
}

private fun handleMediumEnemyCollision(
    enemy: MediumEnemy,
    enemyIterator: MutableListIterator<Enemy>,
    bulletIterator: MutableIterator<Bullet>
) {
    if (enemy.lives > 0) {
        enemyIterator.set(
            enemy.copy(
                radius = enemy.radius + 10,
                lives = enemy.lives - 1
            )
        )
        bulletIterator.remove()
    } else {
        bulletIterator.remove()
        enemyIterator.remove()
    }
}

private fun handleEasyEnemyCollision(
    enemy: EasyEnemy,
    enemyIterator: MutableListIterator<Enemy>,
    bulletIterator: MutableIterator<Bullet>,
    points: Int
) {
    bulletIterator.remove()
    enemyIterator.remove()
}