package com.cyphermoon.tictaczone.presentation.game_flow

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyphermoon.tictaczone.GameModes
import com.cyphermoon.tictaczone.ScreenRoutes
import com.cyphermoon.tictaczone.presentation.main.components.Logo
import com.cyphermoon.tictaczone.presentation.main.components.UserAvatar
import com.cyphermoon.tictaczone.redux.GameMode
import com.cyphermoon.tictaczone.redux.GameModeActions
import com.cyphermoon.tictaczone.redux.GamePlayerProps
import com.cyphermoon.tictaczone.redux.LocalPlayActions
import com.cyphermoon.tictaczone.redux.LocalPlayersProps
import com.cyphermoon.tictaczone.redux.store
import com.cyphermoon.tictaczone.ui.theme.Accent


// Data class for AI characters
data class AICharacter(
    val name: String,
    val id: String,
    val difficulty: String
)

val aiCharacters = listOf(
    AICharacter(name = "Rookie Robot", id = "moon-1", difficulty = "easy"),
    AICharacter(name = "Clever Cyborg", id = "stable-xxl", difficulty = "medium"),
    AICharacter(name = "Master Mindbot", id = "moon-3", difficulty = "hard")
)

@Composable
fun AICard(aiCharacter: AICharacter, handleAIDifficultyChange: (AICharacter) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .padding(2.dp)
            .clickable { handleAIDifficultyChange(aiCharacter) },
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(
            1.dp,
            when (aiCharacter.difficulty) {
                "easy" -> Color.Green
                "medium" -> Color.Blue
                "hard" -> Accent
                else -> MaterialTheme.colorScheme.primary
            }
        )

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(450.dp)
                .padding(16.dp)
        ) {
            Text(text = aiCharacter.difficulty, style = MaterialTheme.typography.bodyLarge)
            UserAvatar(imageUrl = null, name = aiCharacter.name, id = aiCharacter.id)
            Text(
                text = aiCharacter.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight(500)
            )
        }
    }
}


@Composable
fun AICharacters(navController: NavController?) {

    var userState by remember { mutableStateOf(store.getState().user) }

// This function is triggered when an AI character is clicked.
fun handleAICharacterClicked(player2: AICharacter): Unit {
    // Create a GamePlayerProps object for the first player (the user).
    // The ID and name are taken from the current user state.
    // The mark is set to "X" and the score is initialized to 0.
    val player1 = GamePlayerProps(
        id = userState.id,
        name = userState.name,
        mark = "X",
        score = 0,
    )

    // Dispatch an action to update the players in the local play configuration.
    // The players are set to the newly created player1 and the AI character that was clicked.
    store.dispatch(
        LocalPlayActions.UpdatePlayers(
            LocalPlayersProps(
                player1 = player1,
                player2 = GamePlayerProps(
                    id = player2.id,
                    name = player2.name,
                    mark = "O",
                    score = 0,
                    difficulty = player2.difficulty
                )
            )
        )
    )

    // Dispatch an action to update the current player in the local play configuration.
    // The current player is set to player1 (the user).
    store.dispatch(LocalPlayActions.UpdateCurrentPlayer(player1))

    // Dispatch an action to update the game mode in the game mode configuration.
    // The game mode is set to AI.
    store.dispatch(GameModeActions.UpdateGameMode(GameMode(mode = GameModes.AI.mode)))

    // Navigate to the ConfigScreen route.
    navController?.navigate(ScreenRoutes.ConfigScreen.route)
}


    // create a list of AI characters
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Logo(name = null)

        LazyColumn {
            items(aiCharacters.size) { aiCharacter ->
                AICard(
                    aiCharacter = aiCharacters[aiCharacter],
                    handleAIDifficultyChange = ::handleAICharacterClicked
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewAICharacters() {
    AICharacters(navController = null)
}