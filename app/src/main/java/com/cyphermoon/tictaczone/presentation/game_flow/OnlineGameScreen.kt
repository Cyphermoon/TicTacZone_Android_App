package com.cyphermoon.tictaczone.presentation.game_flow

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun OnlineGameScreen(navController: NavController, gameId: String?){
    Column {
        Text(text = "Online GameScreen")
    }
}