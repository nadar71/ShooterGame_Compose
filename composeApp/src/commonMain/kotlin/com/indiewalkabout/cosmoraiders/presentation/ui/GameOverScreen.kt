package com.indiewalkabout.cosmoraiders.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameOverScreen(
    finalScore: Int,
    highScore: Int,
    onPlayAgain: () -> Unit,
    onExitToMenu: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Game Over", fontSize = 32.sp)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Final Score: $finalScore", fontSize = 24.sp)
        Text("High Score: $highScore", fontSize = 24.sp)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onPlayAgain) {
            Text("Play Again")
        }
        
        Button(onClick = onExitToMenu) {
            Text("Exit to Menu")
        }
    }
}
