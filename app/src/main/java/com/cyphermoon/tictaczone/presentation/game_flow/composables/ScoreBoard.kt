package com.cyphermoon.tictaczone.presentation.game_flow.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cyphermoon.tictaczone.presentation.main.components.UserAvatar
import com.cyphermoon.tictaczone.redux.GamePlayerProps

@Composable
fun ScoreBoard(rounds: Int, draws: Int, player1: GamePlayerProps, player2: GamePlayerProps, bestOf: Int?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        Text(text = "$rounds round${if (rounds > 1) "s" else ""}")
        if (draws != 0) {
            Text(text = "($draws draw${if (draws > 1) "s" else ""})")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Profile(player = player1)
            Text(text = "VS", style = MaterialTheme.typography.headlineLarge)
            Profile(player = player2)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (bestOf != null) {
            Text(text = "First to $bestOf Wins", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun Profile(player: GamePlayerProps) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // TODO: Replace with actual image loading logic
        player.name?.let {
            UserAvatar(
                imageUrl = null,
                name = it,
                id = player.id!!,
                modifier = Modifier.size(100.dp)
            )
        }
        player.mark?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight(500)
            )
        }
        Spacer(modifier =Modifier.height(8.dp))
        player.name?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium) }
        Spacer(modifier =Modifier.height(8.dp))
        Text(text = player.score.toString(), style = MaterialTheme.typography.headlineLarge)
        Text(text = "Win${if (player.score > 1) "s" else ""}", style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun ScoreBoardPreview() {
    val player1 = GamePlayerProps(
        name = "Player 1",
        id = "1",
        mark = "X",
        score = 0
    )

    val player2 = GamePlayerProps(
        name = "Player 2",
        id = "2",
        mark = "O",
        score = 0
    )

    ScoreBoard(
        rounds = 5,
        draws = 2,
        player1 = player1,
        player2 = player2,
        bestOf = 10
    )
}

