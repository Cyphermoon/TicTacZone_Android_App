package com.cyphermoon.tictaczone.presentation.game_flow.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TicTacToeBoard(
    label: String,
    board: Map<String, String>,
    handleCellClicked: (String) -> Unit,
    currentMarker: String,
    distortedGhost: Boolean,
    className: String,
    player1Id: String?,
    player2Id: String?,
    distortedMode: Boolean = false
) {
    // TODO: Implement the equivalent of the useState hook in Compose
    // This might involve creating a mutable state using remember { mutableStateOf() }

    val positions by remember { mutableStateOf(board.keys.toList()) }

    // TODO: Implement the shuffleArray function and the gameMode state

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .clip(RoundedCornerShape(16.dp))
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)

        // Displaying Cells
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(positions) { position ->
                Cell(
                    boxType = board[position] ?: "",
                    handleCellClicked = handleCellClicked,
                    position = position,
                    ghost = distortedGhost
                )
            }
        }
    }
}

@Composable
fun Cell(
    boxType: String,
    handleCellClicked: (String) -> Unit,
    position: String,
    ghost: Boolean
) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(key1 = boxType) {
        scale.animateTo(
            targetValue = if (boxType.isNotEmpty()) 1.5f else 1f,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { handleCellClicked(position) }
            .border(1.dp, Color.Gray)
            .scale(scale.value)
    ) {
        if (ghost) {
            Text(
                text = position,
                color = Color.Gray.copy(alpha = 0.5f),
                fontSize = 24.sp
            )
        } else {
            AnimatedContent(targetState = boxType) { type ->
                Text(
                    text = type,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TicTacToeBoardPreview() {
    val board = mapOf(
        "1" to "X",
        "2" to "O",
        "3" to "",
        "4" to "X",
        "5" to "O",
        "6" to "",
        "7" to "X",
        "8" to "O",
        "9" to ""
    )

    TicTacToeBoard(
        label = "Tic Tac Toe",
        board = board,
        handleCellClicked = {},
        currentMarker = "X",
        distortedGhost = false,
        className = "TicTacToeBoard",
        player1Id = "1",
        player2Id = "2",
        distortedMode = false
    )
}