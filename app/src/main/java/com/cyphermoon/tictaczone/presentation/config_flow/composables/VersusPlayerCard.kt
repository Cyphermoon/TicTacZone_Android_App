package com.cyphermoon.tictaczone.presentation.config_flow.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyphermoon.tictaczone.presentation.main.components.UserAvatar
import com.cyphermoon.tictaczone.redux.LocalPlayersProps
import com.cyphermoon.tictaczone.redux.Player
import com.cyphermoon.tictaczone.redux.PlayerProps

// Data class to represent a Player


// Enum to represent the view mode
enum class ViewModeType {
    EDIT
}

// Union Type
sealed class PlayerUnionType {
    data class Player1(val player: PlayerProps?) : PlayerUnionType()
    data class Player2(val player: Player?): PlayerUnionType()
}



// Composable function to represent the VersusPlayerCard
@Composable
fun VersusPlayerCard(players: LocalPlayersProps, startGame: () -> Unit, mode: ViewModeType) {
    // A Column Composable to arrange its children vertically
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        // A Row Composable to arrange its children horizontally
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display the first player's card
            PlayerCard(player = PlayerUnionType.Player1(players.player1))
            // Display the "VS" text
            Text(text = "VS", fontSize = 20.sp)
            // Display the second player's card
            PlayerCard(player = PlayerUnionType.Player2(players.player2))

    }
}

}

// Composable function to represent a PlayerCard
@Composable
fun PlayerCard(player: PlayerUnionType) {
    // A Column Composable to arrange its children vertically
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
                    .width(200.dp)
    ) {
        // Check the type of the player
        val playerName = when (player) {
            is PlayerUnionType.Player1 -> player.player?.name
            is PlayerUnionType.Player2 -> player.player?.name
        }

        val playerId = when (player) {
            is PlayerUnionType.Player1 -> player.player?.id
            is PlayerUnionType.Player2 -> player.player?.id
        }

        val playerMark = when (player) {
            is PlayerUnionType.Player1 -> player.player?.mark
            is PlayerUnionType.Player2 -> player.player?.mark
        }

        if(playerId != null){
        // Display the player's avatar
        UserAvatar(name = playerName ?: "id", id = playerId ?: "", imageUrl = null)
        // Display the player's mark
        Text(text = playerMark ?: "", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        // Display the player's name
        Text(text = playerName ?: "id_2", fontSize = 16.sp)
        }else{
            // Display a player not found message
            Text(text = "Player not found", fontSize = 16.sp)
        }
    }
}







@Preview(showBackground = true)
@Composable
fun PreviewVersusPlayerCard() {
    // Create dummy data
    val player1 = PlayerProps(
        id = "1",
        name = "Player 1",
        isAnonymous = false,
        matches = 10,
        win = 5,
        loss = 5,
        email = "player1@example.com",
        imageUrl = null,
        online = true,
        mark = "X"
    )

    val player2 = Player(
        name = "Player 2",
        id = "2",
        mark = "O"
    )

    val players = LocalPlayersProps(
        player1 = player1,
        player2 = player2
    )

    // Display the VersusPlayerCard with the dummy data
    VersusPlayerCard(players = players, startGame = {}, mode = ViewModeType.EDIT)
}