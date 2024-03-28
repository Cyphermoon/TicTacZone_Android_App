package com.cyphermoon.tictaczone.presentation.main.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cyphermoon.tictaczone.redux.Player
import com.cyphermoon.tictaczone.ui.theme.Accent

@Composable
fun LocalOption(handleUpdateLocalPlayerConfig: (player2: Player) -> Unit?) {
    var player2 by remember { mutableStateOf(Player(name = "")) }

    val handlePlayer2NameChange: (String) -> Unit = { newValue ->
        player2 = player2.copy(name = newValue)
    }

    val handleSubmit: () -> Unit = {
        if (handleUpdateLocalPlayerConfig != null) {
            handleUpdateLocalPlayerConfig(player2.copy(id = "player2"))
            handlePlayer2NameChange("")
        }
    }

    Column(
        modifier = Modifier
            .border(
                width = 2.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        PlayerInput(
            label = "Player 2",
            player = player2,
            handleNameChange = handlePlayer2NameChange
        )
        Button(
            onClick = handleSubmit,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Accent)
        ) {
            Text(text = "Start Game")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerInput( label: String, player: Player, handleNameChange: (String) -> Unit) {
    Column (
        modifier = Modifier.padding(bottom=20.dp)
    ){
        Text(text = label, modifier = Modifier.padding(bottom = 8.dp))
        TextField(
            value = player.name,
            onValueChange = handleNameChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            singleLine = true,
            placeholder = { Text(text = "Enter Player 2 Name")},
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}