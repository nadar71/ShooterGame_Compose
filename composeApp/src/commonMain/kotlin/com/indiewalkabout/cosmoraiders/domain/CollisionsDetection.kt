package com.indiewalkabout.cosmoraiders.domain

import com.indiewalkabout.cosmoraiders.domain.enemy.Enemy
import com.indiewalkabout.cosmoraiders.domain.enemy.StrongEnemy
import com.indiewalkabout.cosmoraiders.domain.enemy.MediumEnemy
import com.indiewalkabout.cosmoraiders.domain.enemy.EasyEnemy
import kotlin.math.sqrt

fun isCollision(bullet: Bullet, enemy: Enemy): Boolean {
    val dx = bullet.x - enemy.x
    val dy = bullet.y - enemy.y.value
    val distance = sqrt(dx * dx + dy * dy)
    return distance < (bullet.radius + enemy.radius)
}

fun checkEnemyCollisions(
    bullets: MutableList<Bullet>,
    enemies: MutableList<Enemy>,
    onCollision: (enemy: Enemy, points: Int) -> Unit,
    onSoundPlay: (index: Int) -> Unit
) {
    val bulletIterator = bullets.iterator()
    while (bulletIterator.hasNext()) {
        val bullet = bulletIterator.next()
        val enemyIterator = enemies.listIterator()
        
        while (enemyIterator.hasNext()) {
            val enemy = enemyIterator.next()
            if (isCollision(bullet, enemy)) {
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
    }
}

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