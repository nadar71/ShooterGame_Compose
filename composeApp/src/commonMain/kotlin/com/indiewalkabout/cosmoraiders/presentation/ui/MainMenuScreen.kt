package com.indiewalkabout.cosmoraiders.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenuScreen() {
    /*Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Cosmo Raiders", fontSize = 32.sp)

        Button(
            onClick = {},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Start Game")
        }
    }*/

    Column(
        modifier = Modifier
            .clickable(enabled = false) { }
            .background(Color.Black.copy(alpha = 0.7f))
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Cosmo Raiders", fontSize = 32.sp)

        Text(
            text = "Ready?",
            fontSize = MaterialTheme.typography.displayMedium.fontSize,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                // stateManager.startNewGame() // TODO : temporary
            }
        ) {
            Text(text = "Start")
        }
    }
}