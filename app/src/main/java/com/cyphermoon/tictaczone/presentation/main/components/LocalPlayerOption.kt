package com.cyphermoon.tictaczone.presentation.main.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cyphermoon.tictaczone.presentation.main.data_class.LocalPlayersProps
import com.cyphermoon.tictaczone.presentation.main.data_class.Player

@Composable
fun LocalOption(handleLocalPlayerStart: ((LocalPlayersProps) -> Unit)?) {
    var player2 by remember { mutableStateOf(Player(name = "")) }

    val handlePlayer2NameChange: (String) -> Unit = { newValue ->
        player2 = player2.copy(name = newValue)
    }

    val handleSubmit: () -> Unit = {
        val localPlayers = LocalPlayersProps(player2 = player2)
//        handleLocalPlayerStart(localPlayers)
    }

    Column(
        modifier = Modifier
            .border(
                width = 2.dp,
                color = Color.DarkGray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        PlayerInput(
            id = "player2",
            label = "Player 2",
            player = player2,
            handleNameChange = handlePlayer2NameChange
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = handleSubmit, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Start Game")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerInput(id: String, label: String, player: Player, handleNameChange: (String) -> Unit) {
    Column {
        Text(text = label, modifier = Modifier.padding(bottom = 8.dp))
        TextField(
            value = player.name,
            onValueChange = handleNameChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
        )
    }
}