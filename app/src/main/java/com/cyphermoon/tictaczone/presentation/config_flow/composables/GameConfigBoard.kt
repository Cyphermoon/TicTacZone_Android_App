package com.cyphermoon.tictaczone.presentation.config_flow.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cyphermoon.tictaczone.ALL_GAME_BOARD
import com.cyphermoon.tictaczone.BoardType
import com.cyphermoon.tictaczone.DEFAULT_GAME_CONFIG
import com.cyphermoon.tictaczone.GameConfigType
import com.cyphermoon.tictaczone.ui.theme.Accent
import com.cyphermoon.tictaczone.ui.theme.LightSecondary
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameConfigBoard(
    game: GameConfigType,
    mode: ViewModeType,
    onGameStart: () -> Unit,
    onTimerChange: (Int) -> Unit,
    onRoundsToWinChange: (Int) -> Unit,
    onDistortedModeChange: (Boolean) -> Unit,
    onBoardTypeChange: (String) -> Unit
) {
    // Define mutable state for the distorted mode switch
    val distortedMode = remember { mutableStateOf(game.distortedMode) }

    // Define mutable state for the selected board type
    val selectedBoardType by remember { mutableStateOf(game.currentBoardType) }

    val isDropdownMenuExpanded = remember { mutableStateOf(false) }

    fun toggleDropdownMenu() {
        isDropdownMenuExpanded.value = !isDropdownMenuExpanded.value
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightSecondary)
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp)),
        horizontalAlignment = Alignment.Start
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Board Variation", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.width(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp)
                        .clickable { toggleDropdownMenu() }
                ) {
                    Text(
                        text = selectedBoardType.value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown Icon",
                        tint = Color.LightGray
                    )
                }
            }

            DropdownMenu(
                expanded = isDropdownMenuExpanded.value,
                onDismissRequest = { isDropdownMenuExpanded.value = false }
            ) {
                ALL_GAME_BOARD.boardType.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(text = type.value) },
                        onClick = {
                            onBoardTypeChange(type.id)
                            isDropdownMenuExpanded.value = false
                        })
                }
            }
        }
        // Display the board variation options


        Spacer(modifier = Modifier.height(16.dp))

        // Display the timer options
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Timer(s)", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp)),
                value = game.timer.toString(),
                onValueChange = { if (it.isNotEmpty()) onTimerChange(it.toInt()) else onTimerChange(0) },
                enabled = mode == ViewModeType.EDIT,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.LightGray,
                    focusedIndicatorColor = Color.DarkGray,
                    unfocusedIndicatorColor = Color.LightGray
                )

            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the rounds to win options
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "First to Win", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp)),
                value = game.roundsToWin.toString(),
                onValueChange = { if (it.isNotEmpty()) onRoundsToWinChange(it.toInt()) else onRoundsToWinChange(0) },
                enabled = mode == ViewModeType.EDIT,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.LightGray,
                    focusedIndicatorColor = Color.DarkGray,
                    unfocusedIndicatorColor = Color.LightGray
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the distorted mode switch
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Distorted Mode", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.width(16.dp))

            Switch(
                checked = distortedMode.value,
                onCheckedChange = { distortedMode.value = it; onDistortedModeChange(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.secondary,
                    checkedTrackColor = MaterialTheme.colorScheme.tertiary,
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the start game button
        Button(
            onClick = onGameStart,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Accent)
        ) {
            Text(text = "Start Game")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewGameConfigBoard() {
    val dummyBoardType = BoardType(
        dimension = "3",
        value = "3x3 Board",
        id = "3x3"
    )

    val dummyGameConfig = GameConfigType(
        currentBoardType = dummyBoardType,
        timer = 10,
        totalRounds = 3,
        roundsToWin = 2,
        distortedMode = true,
        revealMode = false
    )

    GameConfigBoard(
        game = dummyGameConfig,
        mode = ViewModeType.EDIT,
        onGameStart = {},
        onTimerChange = {},
        onRoundsToWinChange = {},
        onDistortedModeChange = {},
        onBoardTypeChange = {}
    )

}