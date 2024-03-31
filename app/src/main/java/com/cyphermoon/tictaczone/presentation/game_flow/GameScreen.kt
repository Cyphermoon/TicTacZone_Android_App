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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyphermoon.tictaczone.presentation.game_flow.composables.PlayerScore
import com.cyphermoon.tictaczone.presentation.game_flow.composables.ScoreBoard
import com.cyphermoon.tictaczone.presentation.game_flow.composables.TicTacToeBoard
import com.cyphermoon.tictaczone.presentation.game_flow.utils.checkIfBoardIsFull
import com.cyphermoon.tictaczone.presentation.game_flow.utils.checkWinningMove
import com.cyphermoon.tictaczone.presentation.game_flow.utils.isValidMove
import com.cyphermoon.tictaczone.presentation.game_flow.utils.resetBoard
import com.cyphermoon.tictaczone.presentation.game_flow.utils.showGameStateDialog
import com.cyphermoon.tictaczone.redux.GamePlayerProps
import com.cyphermoon.tictaczone.redux.LocalPlayActions
import com.cyphermoon.tictaczone.redux.store
import kotlin.math.roundToInt

@Composable
fun GameScreen() {
    var localPlay by remember { mutableStateOf(store.getState().localPlay) }

    var timerPercentage =
        (localPlay.countdown.toDouble() / localPlay.gameConfig!!.timer.toDouble() * 100).roundToInt()
    val context = LocalContext.current

    /**
     * This function resets the scores of both players to 0 after a full round.
     * A full round is considered complete when one of the players has won the required number of games (roundsToWin).
     */
    fun resetAfterFullRound() {
        // Retrieve the player1 and player2 from the localPlay state
        val player1 = localPlay.players?.player1
        val player2 = localPlay.players?.player2

        // Check if both players are not null
        if (player1 == null || player2 == null) return

        // If both players are not null, reset their scores to 0
        // Dispatch an action to update player1's score in the state
        store.dispatch(LocalPlayActions.UpdatePlayer1(player1.copy(score = 0)))
        // Dispatch an action to update player2's score in the state
        store.dispatch(LocalPlayActions.UpdatePlayer2(player2.copy(score = 0)))
        // Dispatch an action to update the current round to 1
        store.dispatch(LocalPlayActions.UpdateCurrentRound(1))

    }

    fun handleCellClicked(position: String) {
        // Get the current state of the game
        val currentPlayer = localPlay.currentPlayer
        val player1 = localPlay.players?.player1
        val player2 = localPlay.players?.player2
        val roundsToWin = localPlay.gameConfig?.roundsToWin

        // Check if the current player is valid
        if (currentPlayer == null || player1 == null || player2 == null || roundsToWin == null) return

        // Check if the move is valid
        if (!isValidMove(localPlay.board, position)) return

        // Update the board with the current player's mark
        val newBoard = localPlay.board.toMutableMap()
        newBoard[position] = currentPlayer.mark ?: ""
        store.dispatch(LocalPlayActions.UpdateBoard(newBoard))

        // Check if the current player has won
        if (checkWinningMove(newBoard, currentPlayer.mark ?: "")) {
            // Update the player's score and check if they have won the game
            if (currentPlayer.id == player1.id) {
                val score = player1.score + 1
                if (score <= roundsToWin) {
                    store.dispatch(LocalPlayActions.UpdatePlayer1(player1.copy(score = score)))
                    // If the player has won the game, show a dialog
                    if (score == roundsToWin) {
                        showGameStateDialog(
                            context,
                            "Game Over",
                            "${currentPlayer.name} has won the game!",
                            resetBoard = { resetAfterFullRound() }
                        )
                    }
                }
            } else {
                val score = player2.score + 1
                if (score <= roundsToWin) {
                    store.dispatch(LocalPlayActions.UpdatePlayer2(player2.copy(score = score)))
                    // If the player has won the game, show a dialog
                    if (score == roundsToWin) {
                        showGameStateDialog(
                            context,
                            "Game Over",
                            "${currentPlayer.name} has won the game!",
                            resetBoard = { resetAfterFullRound() })
                    }
                }
            }
            // Increment the current round
            store.dispatch(LocalPlayActions.UpdateCurrentRound(localPlay.currentRound + 1))

            // Reset the board
            store.dispatch(LocalPlayActions.UpdateBoard(resetBoard()))
        }

        // Check if the game is a draw
        if (checkIfBoardIsFull(newBoard)) {
            // Show a dialog indicating the game is a draw
            showGameStateDialog(context, "Game Over", "The game is a draw!", resetBoard={resetAfterFullRound()})
            // Update the draws count
            store.dispatch(LocalPlayActions.UpdateDraws(localPlay.draws + 1))
            // Reset the board
            store.dispatch(LocalPlayActions.UpdateBoard(resetBoard()))
            // Increment the current round
            store.dispatch(LocalPlayActions.UpdateCurrentRound(localPlay.currentRound + 1))
        }

        // Switch to the other player
        val nextPlayer = if (currentPlayer.id == player1.id) player2 else player1
        store.dispatch(LocalPlayActions.UpdateCurrentPlayer(nextPlayer))
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
                currentPlayer = localPlay.currentPlayer,
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
                rounds = localPlay.currentRound ?: 0,
                draws = localPlay.draws, // Update this as per your logic
                player1 = localPlay.players?.player1!!,
                player2 = localPlay.players?.player2!!,
                bestOf = localPlay.gameConfig?.roundsToWin
            )
        } else {
            Text("Players or current player doesn't exist.")
        }
    }
}

