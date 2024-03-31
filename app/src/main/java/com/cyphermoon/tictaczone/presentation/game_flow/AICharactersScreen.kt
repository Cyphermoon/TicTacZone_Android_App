package com.cyphermoon.tictaczone.presentation.game_flow

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cyphermoon.tictaczone.presentation.main.components.Logo
import com.cyphermoon.tictaczone.presentation.main.components.UserAvatar
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
fun AICard(aiCharacter: AICharacter, handleAIDifficultyChange: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .padding(2.dp)
            .clickable { handleAIDifficultyChange(aiCharacter.difficulty) },
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
fun AICharacters() {

    fun handleAIDifficultyChange(difficulty: String) {
        Log.v("New Difficulty", "Difficult is now $difficulty")
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
                    handleAIDifficultyChange = ::handleAIDifficultyChange
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewAICharacters() {
    AICharacters()
}