package com.indiewalkabout.cosmoraiders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.indiewalkabout.cosmoraiders.domain.game.Game
import com.indiewalkabout.cosmoraiders.domain.game.GameStateManager
import org.koin.compose.koinInject

@Composable
fun MainMenuScreen(
    onStartGame: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Cosmo Raiders", fontSize = 32.sp)
        
        Button(
            onClick = onStartGame,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {

            Text("Start Game")
        }
    }
}
