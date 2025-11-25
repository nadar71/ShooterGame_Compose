package com.indiewalkabout.cosmoraiders

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameOverScreen(
    finalScore: Int,
    highScore: Int,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Game Over!",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Text(
            text = "Final Score: $finalScore",
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp)
        )
        
        Text(
            text = "High Score: $highScore",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onPlayAgain,
                modifier = Modifier.width(200.dp)
            ) {
                Text("Play Again")
            }
            
            Button(
                onClick = onExit,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.width(200.dp)
            ) {
                Text("Exit to Menu")
            }
        }
    }
}
