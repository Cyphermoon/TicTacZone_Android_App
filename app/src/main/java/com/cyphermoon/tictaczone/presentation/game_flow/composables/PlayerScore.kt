package com.cyphermoon.tictaczone.presentation.game_flow.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cyphermoon.tictaczone.presentation.main.components.CircularProgress
import com.cyphermoon.tictaczone.presentation.main.components.UserAvatar
import com.cyphermoon.tictaczone.redux.GamePlayerProps
import com.cyphermoon.tictaczone.ui.theme.Accent
import com.cyphermoon.tictaczone.ui.theme.LightSecondary

@Composable
fun PlayerScore(
    imageURL: String?,
    countdownText: String? = null,
    countdown: Int = 0,
    totalTimer: Int = 100,
    currentPlayer: GamePlayerProps?,
    handleDistortedMode: () -> Unit,
    distortedMode: Boolean = false
) {
    val percentage = (countdown.toFloat() / totalTimer.toFloat())

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        if (currentPlayer != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Current Player", style = MaterialTheme.typography.labelSmall)

                    // TODO: Replace with actual image loading logic

                    currentPlayer.name?.let {
                        UserAvatar(
                            imageUrl = imageURL,
                            name = it,
                            id = currentPlayer.id!!,
                            modifier = Modifier.size(180.dp)
                        )
                    }
                    currentPlayer.name?.let { Text(text = it, style = MaterialTheme.typography.labelMedium) }
                    // Distorted Mode button
                    if(distortedMode){
                        Button(
                            onClick = {  handleDistortedMode() },
                            colors = ButtonDefaults.buttonColors(containerColor = Accent)

                        ) {
                            Text(text = "Show Distorted")
                        }
                    }

                }

                if (countdown != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Timer", style = MaterialTheme.typography.labelSmall)
                        // TODO: Implement CircularBar composable
                        CircularProgress(percentage = percentage, text= countdownText)
                    }
                }
            }
        } else {
            Text(text = "Current Player Couldn't be found please start again")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PlayerScorePreview() {
    val player = GamePlayerProps(
        name = "Player 1",
        id = "1",
        mark = "X",
        score = 0
    )

    PlayerScore(
        imageURL = null,
        countdown = 40,
        totalTimer = 100,
        countdownText = "4s",
        currentPlayer = player,
        handleDistortedMode = {}
    )
}