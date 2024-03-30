package com.cyphermoon.tictaczone.presentation.game_flow

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyphermoon.tictaczone.presentation.game_flow.composables.PlayerScore
import com.cyphermoon.tictaczone.presentation.game_flow.composables.ScoreBoard
import com.cyphermoon.tictaczone.presentation.game_flow.composables.TicTacToeBoard
import com.cyphermoon.tictaczone.redux.GamePlayerProps
import com.cyphermoon.tictaczone.redux.LocalPlayActions
import com.cyphermoon.tictaczone.redux.store
import kotlin.math.roundToInt

@Composable
fun GameScreen() {
    var localPlay by remember { mutableStateOf(store.getState().localPlay) }
    var timerPercentage = (localPlay.countdown.toDouble() / localPlay.gameConfig!!.timer.toDouble() * 100).roundToInt()


    fun handleCellClicked(position: String) {
        val newBoard = localPlay.board.toMutableMap()
        newBoard[position] = localPlay.currentPlayer?.mark ?: ""
        store.dispatch(LocalPlayActions.UpdateBoard(newBoard))
    }
    DisposableEffect(key1 = store.getState().localPlay, key2 = store.getState().localPlay.board) {
        val unsubscribe = store.subscribe {
            // Update localPlay state whenever the state in the store changes
            localPlay = store.getState().localPlay
        }

        onDispose {
            // Unsubscribe when the composable is disposed
            unsubscribe()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (localPlay.players != null && localPlay.currentPlayer != null) {
            // Extract properties from PlayerProps and Player
            PlayerScore(
                imageURL = null,
                countdown = timerPercentage,
                countdownText = "${localPlay.countdown}s",
                player1 = localPlay?.players!!.player1!!,
                player2 = localPlay?.players!!.player2!!,
                currentPlayer =  localPlay.currentPlayer,
                setCurrentPlayer = { store.dispatch(LocalPlayActions.UpdateCurrentPlayer(it)) }
            )

            localPlay.currentPlayer?.mark?.let {
                TicTacToeBoard(
                    label = "Tic Tac Toe",
                    board = localPlay.board,
                    handleCellClicked = ::handleCellClicked,
                    currentMarker = it,
                    distortedGhost = false,
                    className = "TicTacToeBoard",
                    player1Id = localPlay.players?.player1?.id,
                    player2Id = localPlay.players?.player1?.id,
                    distortedMode = false
                )
            } ?: Text("Current player's mark doesn't exist.")

            Spacer(modifier = Modifier.height(15.dp))
            ScoreBoard(
                rounds = localPlay.gameConfig?.totalRounds ?: 0,
                draws = localPlay.draws, // Update this as per your logic
                player1 = localPlay.players?.player1!!,
                player2 = localPlay.players?.player2!!,
                bestOf = localPlay.gameConfig?.roundsToWin
            )        } else {
            Text("Players or current player doesn't exist.")
        }
    }
}

