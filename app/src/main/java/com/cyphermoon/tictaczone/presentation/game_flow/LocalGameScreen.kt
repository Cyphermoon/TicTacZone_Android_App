package com.cyphermoon.tictaczone.presentation.game_flow

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyphermoon.tictaczone.GameModes
import com.cyphermoon.tictaczone.presentation.game_flow.composables.PlayerScore
import com.cyphermoon.tictaczone.presentation.game_flow.composables.ScoreBoard
import com.cyphermoon.tictaczone.presentation.game_flow.composables.TicTacToeBoard
import com.cyphermoon.tictaczone.presentation.game_flow.utils.TimerUtils
import com.cyphermoon.tictaczone.presentation.game_flow.utils.checkIfBoardIsFull
import com.cyphermoon.tictaczone.presentation.game_flow.utils.checkWinningMove
import com.cyphermoon.tictaczone.presentation.game_flow.utils.generateEasyAIMove
import com.cyphermoon.tictaczone.presentation.game_flow.utils.generateHardAIMove
import com.cyphermoon.tictaczone.presentation.game_flow.utils.generateMediumAIMove
import com.cyphermoon.tictaczone.presentation.game_flow.utils.isValidMove
import com.cyphermoon.tictaczone.presentation.game_flow.utils.resetAfterFullRound
import com.cyphermoon.tictaczone.presentation.game_flow.utils.resetBoard
import com.cyphermoon.tictaczone.presentation.game_flow.utils.showGameStateDialog
import com.cyphermoon.tictaczone.redux.LocalPlayActions
import com.cyphermoon.tictaczone.redux.store
import kotlinx.coroutines.delay
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.math.roundToInt

@Composable
fun LocalGameScreen() {
    var localPlay by remember { mutableStateOf(store.getState().localPlay) }
    // Add a state to track whether the modal is open
    var isModalOpen by remember { mutableStateOf(false) }
    var distortedGhost by rememberSaveable { mutableStateOf(false) }

    val timerPercentage =
        (localPlay.countdown.toDouble() / localPlay.gameConfig!!.timer.toDouble() * 100).roundToInt()
    val gameMode by remember { mutableStateOf(store.getState().gameMode.mode) }
    val context = LocalContext.current
    val isAITurn = localPlay.currentPlayer?.id == localPlay.players?.player2?.id
    var isThinking by remember { mutableStateOf(false) }
    var thinkingTime by remember { mutableIntStateOf(0) }


    /**
     * This function resets the scores of both players to 0 after a full round.
     * A full round is considered complete when one of the players has won the required number of games (roundsToWin).
     */


    fun resetGameState(){
        // Retrieve the player1 and player2 from the localPlay state
        val player1 = localPlay.players?.player1
        val player2 = localPlay.players?.player2

        // Set Modal state to false


        resetAfterFullRound(localPlay.gameConfig?.timer, player1, player2, onFinish = {isModalOpen = false})

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
                            resetBoard = { resetGameState() }
                        )
                        // Set the modal state to true
                        isModalOpen = true
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
                            resetBoard = { resetGameState() }
                        )
                        // Set modal state to true
                        isModalOpen = true
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
            showGameStateDialog(
                context,
                "Game Over",
                "The game is a draw!",
                resetBoard = { resetGameState() })
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

        // Reset the countdown
        store.dispatch(LocalPlayActions.UpdateCountdown(localPlay.gameConfig?.timer ?: 0))
    }

    fun toggleDistortedGhost() {
        distortedGhost = true
        Timer("DistortedGhost", false).schedule(5000) {
            distortedGhost = false
        }
    }

    DisposableEffect(key1 = localPlay.currentRound, key2 = isModalOpen) {
        if (isModalOpen) {
            TimerUtils.stopTimer()
        } else {

            TimerUtils.startTimer {
                // This block of code will be executed every second
                // 'timerTask' is the current TimerTask instance
                // Get the current state of the game.
                val localGameState = store.getState().localPlay
                // If the countdown is greater than 0, decrement it and update the redux store.
                if (localGameState.countdown > 0) {
                    store.dispatch(LocalPlayActions.UpdateCountdown(localGameState.countdown - 1))
                    println("Countdown: ${localGameState.countdown}")
                } else {
                    // If the countdown has reached 0, cancel the timer and print a message.
                    TimerUtils.stopTimer()
                    println("Countdown finished")

                    // Increase the total rounds
                    store.dispatch(LocalPlayActions.UpdateCurrentRound(localGameState.currentRound + 1))
                    // Reset the countdown
                    store.dispatch(
                        LocalPlayActions.UpdateCountdown(
                            localGameState.gameConfig?.timer ?: 0
                        )
                    )

                    // Update the score of the other player
                    val otherPlayer =
                        if (localGameState.currentPlayer?.id == localGameState.players?.player1?.id) localGameState.players?.player2 else localGameState.players?.player1
                    if (otherPlayer != null) {
                        store.dispatch(
                            if (otherPlayer.id == localGameState.players?.player1?.id) LocalPlayActions.UpdatePlayer1(
                                otherPlayer.copy(score = otherPlayer.score + 1)
                            ) else LocalPlayActions.UpdatePlayer2(otherPlayer.copy(score = otherPlayer.score + 1))
                        )
                    }

                    // Switch the current player
                    otherPlayer?.let { LocalPlayActions.UpdateCurrentPlayer(it) }
                        ?.let { store.dispatch(it) }
                }
            }
        }

        onDispose {
            // Stop the timer when the composable is disposed
            TimerUtils.stopTimer()
        }
    }

    LaunchedEffect(
        key1 = localPlay.players?.player1?.score,
        key2 = localPlay.players?.player2?.score,
        key3 = localPlay.gameConfig?.roundsToWin
    ) {
        val roundsToWin = localPlay.gameConfig?.roundsToWin ?: 0
        val player1 = localPlay.players?.player1
        val player2 = localPlay.players?.player2

        // Check if a player has won the game
        if ((player1?.score ?: 0) >= roundsToWin || (player2?.score ?: 0) >= roundsToWin) {
            // Pause the game and open the modal
            isModalOpen = true
            TimerUtils.stopTimer()
        }

        // Check if player 1 has won the game
        if (player1 != null && player1.score >= roundsToWin) {
            // Set player1 as the winner
            showGameStateDialog(
                context,
                "Game Over",
                "${player1.name} has won the game!",
                resetBoard = { resetGameState() }
            )
        }

        // Check if player 2 has won the game
        if (player2 != null && player2.score >= roundsToWin) {
            // Set player2 as the winner
            showGameStateDialog(
                context,
                "Game Over",
                "${player2.name} has won the game!",
                resetBoard = { resetGameState() }
            )
        }
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


// Check if the gameMode is AI and handle player2 as an AI player
    if (gameMode == GameModes.AI.mode && isAITurn) {
        LaunchedEffect(Unit) {
            val configTime = localPlay.gameConfig!!.timer - 2

            isThinking = true
            thinkingTime = (1..configTime).random() // Random delay between 1 and 3 seconds

            // Simulate delay
            delay(thinkingTime * 1000L)

            val player2Difficulty = localPlay.players?.player2?.difficulty
            val player1 = localPlay.players?.player1
            val player2 = localPlay.players?.player2

            if (player1 != null && player2 != null) {
                val move = when (player2Difficulty) {
                    "easy" -> generateEasyAIMove(localPlay.board)
                    "medium" -> generateMediumAIMove(
                        localPlay.board.toMutableMap(),
                        player1,
                        player2
                    )

                    "hard" -> generateHardAIMove(localPlay.board.toMutableMap(), player1, player2)
                    else -> null
                }

                if (move != null) {
                    handleCellClicked(move)
                }
            }

            // Set isThinking to false after making the move
            isThinking = false
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
                currentPlayer = localPlay.currentPlayer,
                handleDistortedMode = { toggleDistortedGhost() },
                distortedMode = localPlay.gameConfig!!.distortedMode
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "I'm not thinking, just building suspense for $thinkingTime seconds",
                color = if (isThinking && isAITurn) Color.Gray else Color.Transparent,
                fontSize = 14.sp
            )
            localPlay.currentPlayer?.mark?.let {
                TicTacToeBoard(
                    label = localPlay.gameConfig!!.currentBoardType.value,
                    board = localPlay.board,
                    handleCellClicked = ::handleCellClicked,
                    currentMarker = it,
                    distortedGhost = distortedGhost,
                    className = "TicTacToeBoard",
                    player1Id = localPlay.players?.player1?.id,
                    player2Id = localPlay.players?.player1?.id,
                    distortedMode = localPlay.gameConfig!!.distortedMode,
                    disableBoard = isThinking && isAITurn
                )
            } ?: Text("Current player's mark doesn't exist.")

            Spacer(modifier = Modifier.height(15.dp))
            ScoreBoard(
                rounds = localPlay.currentRound,
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

