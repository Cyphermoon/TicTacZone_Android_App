package com.cyphermoon.tictaczone.presentation.game_flow

import android.content.ContentValues.TAG
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cyphermoon.tictaczone.DEFAULT_BOARD
import com.cyphermoon.tictaczone.DEFAULT_GAME_CONFIG
import com.cyphermoon.tictaczone.GameModes
import com.cyphermoon.tictaczone.presentation.game_flow.composables.PlayerScore
import com.cyphermoon.tictaczone.presentation.game_flow.composables.ScoreBoard
import com.cyphermoon.tictaczone.presentation.game_flow.composables.TicTacToeBoard
import com.cyphermoon.tictaczone.presentation.game_flow.utils.GameHistoryProps
import com.cyphermoon.tictaczone.presentation.game_flow.utils.OnlineGameData
import com.cyphermoon.tictaczone.presentation.game_flow.utils.TimerUtils
import com.cyphermoon.tictaczone.presentation.game_flow.utils.checkIfBoardIsFull
import com.cyphermoon.tictaczone.presentation.game_flow.utils.checkWinningMove
import com.cyphermoon.tictaczone.presentation.game_flow.utils.firestoreDB
import com.cyphermoon.tictaczone.presentation.game_flow.utils.generateEasyAIMove
import com.cyphermoon.tictaczone.presentation.game_flow.utils.generateHardAIMove
import com.cyphermoon.tictaczone.presentation.game_flow.utils.generateMediumAIMove
import com.cyphermoon.tictaczone.presentation.game_flow.utils.increaseOtherPlayerOnlineScore
import com.cyphermoon.tictaczone.presentation.game_flow.utils.isValidMove
import com.cyphermoon.tictaczone.presentation.game_flow.utils.playerToGamePlayerProps
import com.cyphermoon.tictaczone.presentation.game_flow.utils.resetAfterFullRound
import com.cyphermoon.tictaczone.presentation.game_flow.utils.resetBoard
import com.cyphermoon.tictaczone.presentation.game_flow.utils.resetBoardOnline
import com.cyphermoon.tictaczone.presentation.game_flow.utils.resetGameState
import com.cyphermoon.tictaczone.presentation.game_flow.utils.resetScoreOnline
import com.cyphermoon.tictaczone.presentation.game_flow.utils.setOnlineWinner
import com.cyphermoon.tictaczone.presentation.game_flow.utils.showGameStateDialog
import com.cyphermoon.tictaczone.presentation.game_flow.utils.switchPlayer
import com.cyphermoon.tictaczone.presentation.game_flow.utils.updateCountdown
import com.cyphermoon.tictaczone.presentation.game_flow.utils.updateDraws
import com.cyphermoon.tictaczone.presentation.game_flow.utils.updateHistory
import com.cyphermoon.tictaczone.presentation.game_flow.utils.updatePauseStatus
import com.cyphermoon.tictaczone.presentation.game_flow.utils.updatePlayerScore
import com.cyphermoon.tictaczone.presentation.game_flow.utils.updatePlayerStats
import com.cyphermoon.tictaczone.presentation.game_flow.utils.updatePositionInFirestore
import com.cyphermoon.tictaczone.presentation.game_flow.utils.updateTotalRounds
import com.cyphermoon.tictaczone.redux.LocalPlayActions
import com.cyphermoon.tictaczone.redux.LocalPlayConfig
import com.cyphermoon.tictaczone.redux.store
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.math.roundToInt


val onlineGameDataFlow = MutableStateFlow<OnlineGameData?>(null)

fun listenForOnlineGameDataUpdates(gameId: String) {
    val firestoreDB = FirebaseFirestore.getInstance()
    val gameDocRef = firestoreDB.collection("games").document(gameId)

    gameDocRef.addSnapshotListener { snapshot, error ->
        if (error != null) {
            Log.w(TAG, "Listen failed.", error)
            return@addSnapshotListener
        }

        if (snapshot != null && snapshot.exists()) {
            val onlineGameData = snapshot.toObject(OnlineGameData::class.java)
            onlineGameDataFlow.value = onlineGameData
        } else {
            Log.d(TAG, "Current data: null")
        }
    }
}

@Composable
fun OnlineGameScreen(navController: NavController, gameId: String?) {
    val onlineGameData by onlineGameDataFlow.collectAsState()
    var distortedGhost by rememberSaveable { mutableStateOf(false) }
    val currentState by remember { mutableStateOf(store.getState().user) }
    val currentCoroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    suspend fun handlePlayer1Won(gameId: String, gameRep: OnlineGameData) {
        if (gameId.isEmpty()) return

        setOnlineWinner(gameId, gameRep.player1)
        showGameStateDialog(
            context,
            "Game Over",
            "${gameRep.player1.name} has won the game!",
            resetBoard = { resetGameState(gameId, gameRep) }
        )
        updatePauseStatus(gameId, true)

        // Update the game history for both players
        updateHistory(
            gameRep.player1.id,
            GameHistoryProps(
                gameRep.player2.name,
                gameRep.config.currentBoardType.value,
                gameRep.config.roundsToWin,
                "Win"
            )
        )
        updateHistory(
            gameRep.player2.id,
            GameHistoryProps(
                gameRep.player1.name,
                gameRep.config.currentBoardType.value,
                gameRep.config.roundsToWin,
                "Loss"
            )
        )

        // Update both player stats
        updatePlayerStats(gameRep.player1.id, true)
        updatePlayerStats(gameRep.player2.id, false)
    }

    suspend fun handlePlayer2Won(gameId: String, gameRep: OnlineGameData) {
        if (gameId.isEmpty()) return

        setOnlineWinner(gameId, gameRep.player2)
        showGameStateDialog(
            context,
            "Game Over",
            "${gameRep.player2.name} has won the game!",
            resetBoard = { resetGameState(gameId, gameRep) }
        )
        updatePauseStatus(gameId, true)

        // Update the game history for both players
        updateHistory(
            gameRep.player2.id,
            GameHistoryProps(
                gameRep.player1.name,
                gameRep.config.currentBoardType.value,
                gameRep.config.roundsToWin,
                "Win"
            )
        )
        updateHistory(
            gameRep.player1.id,
            GameHistoryProps(
                gameRep.player2.name,
                gameRep.config.currentBoardType.value,
                gameRep.config.roundsToWin,
                "Loss"
            )
        )

        // Update both player stats
        updatePlayerStats(gameRep.player2.id, true)
        updatePlayerStats(gameRep.player1.id, false)
    }


    fun handleCellClicked(
        position: String,
        onlineGameData: OnlineGameData,
        gameId: String
    ) {
        // If the cell at the clicked position is not empty or if there is no online game ID, return
        if (onlineGameData.board[position] != "" || gameId.isEmpty()) return

        // If the current player is not the one who is supposed to make the move, return
//        if (onlineGameData.currentPlayer.id != currentPlayerId) return

        // Update the specific position in the board in Firestore
        val newBoard = onlineGameData.board.toMutableMap()
        newBoard[position] = onlineGameData.currentPlayer.mark

        currentCoroutineScope.launch {
            updatePositionInFirestore(gameId, position, onlineGameData.currentPlayer.mark)
            updateCountdown(gameId, onlineGameData.config.timer)

            // If the current player has won
            if (checkWinningMove(newBoard, onlineGameData.currentPlayer.mark)) {
                //update total rounds online
                updateTotalRounds(gameId, onlineGameData.totalRounds + 1)

                // If the current player is player 1
                if (onlineGameData.currentPlayer.id == onlineGameData.player1.id) {
                    // Increase player 1's score
                    val score = onlineGameData.player1.score + 1

                    // If player 1's score is less than or equal to the number of rounds to win
                    if (score <= onlineGameData.config.roundsToWin) {
                        // Update player 1's score in Firestore
                        updatePlayerScore(gameId, "player1", score)
                    }
                } else {
                    // If the current player is player 2

                    // Increase player 2's score
                    val score = onlineGameData.player2.score + 1

                    // If player 2's score is less than or equal to the number of rounds to win
                    if (score <= onlineGameData.config.roundsToWin) {
                        // Update player 2's score in Firestore
                        updatePlayerScore(gameId, "player2", score)
                    }
                }

                // Reset the board in Firestore
                resetBoardOnline(gameId, DEFAULT_BOARD)
            }

            // If the game is a draw
            if (checkIfBoardIsFull(newBoard)) {
                updateTotalRounds(gameId, onlineGameData.totalRounds + 1)
                updateDraws(gameId, onlineGameData.draws + 1)
                resetBoardOnline(gameId, DEFAULT_BOARD)
            }

            // Switch the current player
            switchPlayer(
                gameId,
                onlineGameData.currentPlayer,
                onlineGameData.player1,
                onlineGameData.player2
            )
        }
    }

    // Updating Game Data
    LaunchedEffect(gameId) {
        gameId?.let {
            listenForOnlineGameDataUpdates(it)
        }
    }

    LaunchedEffect(key1 = onlineGameData?.player1?.score, key2 = onlineGameData?.player2?.score) {
        onlineGameData?.let { game ->
            if (game.player1.score >= game.config.roundsToWin && currentState.id == game.player1.id) {
                handlePlayer1Won(gameId ?: "", game)
            } else if (game.player2.score >= game.config.roundsToWin && currentState.id == game.player2.id) {
                handlePlayer2Won(gameId ?: "", game)
            }
        }
    }

    // Countdown timer logic
//    LaunchedEffect(key1 = onlineGameData?.countdown, key2 = onlineGameData?.currentPlayer?.id) {
//        if (gameId == null) return@LaunchedEffect
//        onlineGameData?.let { game ->
//            if (game.countdown != null && game.currentPlayer.id == currentState.id) {
//                val intervalJob = launch {
//                    while (game.countdown > 0 && !game.pause) {
//                        delay(1000) // wait for 1
//                        Log.v(
//                            "OnlineGameScreen",
//                            "Countdown: ${onlineGameData?.countdown} after countdown updating..."
//                        )
//                        if (game.pause === true) continue // skip this iteration if the game is paused
//                        updateCountdown(gameId ?: "", game.countdown - 1)
//                    }
//                }
//
//                if (game.countdown == 0) {
//                    intervalJob.cancel() // stop the countdown
//                    // Increase other player's score, update total rounds and switch player
//                    increaseOtherPlayerOnlineScore(
//                        game.currentPlayer,
//                        game.player1,
//                        game.player2,
//                        firestoreDB,
//                        gameId ?: ""
//                    )
//                    updateTotalRounds(gameId, game.totalRounds + 1)
//                    switchPlayer(gameId, game.currentPlayer, game.player1, game.player2)
//                    updateCountdown(gameId, game.config.timer)
//                }
//            }
//        }
//    }

    fun toggleDistortedGhost() {
        if(onlineGameData!!.config.distortedMode) {
            distortedGhost = true
            Timer("DistortedGhost", false).schedule(5000) {
                distortedGhost = false
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (onlineGameData != null) {
            PlayerScore(
                imageURL = null,
                countdown = onlineGameData!!.countdown,
                totalTimer = onlineGameData!!.config.timer,
                countdownText = "${onlineGameData!!.countdown}s",
                currentPlayer = playerToGamePlayerProps(onlineGameData!!.currentPlayer),
                handleDistortedMode = { toggleDistortedGhost() },
                distortedMode = onlineGameData!!.config.distortedMode
            )

            Spacer(modifier = Modifier.height(16.dp))

            onlineGameData!!.currentPlayer?.mark?.let {
                TicTacToeBoard(
                    label = onlineGameData!!.config.currentBoardType.value,
                    board = onlineGameData!!.board,
                    handleCellClicked = { position ->
                        handleCellClicked(
                            position,
                            onlineGameData!!,
                            gameId!!
                        )
                    },
                    currentMarker = it,
                    distortedGhost = distortedGhost,
                    className = "TicTacToeBoard",
                    player1Id = onlineGameData!!.player1.id,
                    player2Id = onlineGameData!!.player2.id,
                    distortedMode = onlineGameData!!.config!!.distortedMode,
                    disableBoard = false
                )
            } ?: Text("Current player's mark doesn't exist.")

            Spacer(modifier = Modifier.height(15.dp))
            ScoreBoard(
                rounds = onlineGameData!!.totalRounds,
                draws = onlineGameData!!.draws, // Update this as per your logic
                player1 = playerToGamePlayerProps(onlineGameData!!.player1),
                player2 = playerToGamePlayerProps(onlineGameData!!.player2),
                bestOf = onlineGameData!!.config.roundsToWin
            )
        } else {
            Text("Players or current player doesn't exist.")
        }
    }
}

