package com.cyphermoon.tictaczone.presentation.config_flow.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyphermoon.tictaczone.BoardType
import com.cyphermoon.tictaczone.GameConfigType
import com.cyphermoon.tictaczone.redux.LocalPlayersProps
import com.cyphermoon.tictaczone.redux.Player
import com.cyphermoon.tictaczone.redux.PlayerProps
import com.cyphermoon.tictaczone.redux.store


@Composable
fun ConfigScreen(navController: NavController?) {
    var players by remember { mutableStateOf(store.getState().localPlayConfig.players) }

    // Subscribe to the store
    DisposableEffect(key1 = store.getState().localPlayConfig) {
        val unsubscribe = store.subscribe {
            // Update players state whenever the state in the store changes
            players = store.getState().localPlayConfig.players
        }

        onDispose {
            // Unsubscribe when the composable is disposed
            unsubscribe()
        }
    }

        val dummyBoardType = BoardType(
            dimension = "3",
            value = "3x3 Board",
            id = "3x3"
        )

        val dummyGameConfig = GameConfigType(
            boardType = listOf(dummyBoardType),
            currentBoardType = dummyBoardType,
            timer = 10,
            totalRounds = 3,
            roundsToWin = 2,
            distortedMode = true,
            revealMode = false
        )

    Column {

        // Display the VersusPlayerCard with the dummy data
        if(players != null) {
            VersusPlayerCard(players = players!!, startGame = {}, mode = ViewModeType.EDIT)
        }  else{
            // Display a not found indicator
            Text(text = "One or both players has not been configured")

        }

        Spacer(modifier = Modifier.height(20.dp))

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
}