package com.cyphermoon.tictaczone.presentation.game_flow.utils

import com.cyphermoon.tictaczone.DEFAULT_BOARD
import com.cyphermoon.tictaczone.redux.GamePlayerProps
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

val firestoreDB = FirebaseFirestore.getInstance()

suspend fun updateOrGetGame(gameId: String, data: OnlineGameData): OnlineGameData {
    val gameDocRef = FirebaseFirestore.getInstance().collection("games").document(gameId)

    val docSnap = gameDocRef.get().await()

    if (docSnap.exists()) {
        return docSnap.toObject(OnlineGameData::class.java)!!
    }

    // Merge the provided data with the default game data
    // Merge the provided data with the default game data
    val gameData = data.copy(createdAt = Timestamp.now())
    try {
        gameDocRef.set(gameData, SetOptions.merge()).await()
    } catch (error: Exception) {
        throw Exception("Error creating game: $error")
    }

    return gameData
}

fun playerToGamePlayerProps(player: Player): GamePlayerProps {
    return GamePlayerProps(
        name = player.name,
        id = player.id,
        mark = player.mark,
        score = player.score,
        difficulty = null
    )
}

fun handleTimerChange(newTimer: Int, gameId: String, scope: CoroutineScope) {
    scope.launch {
        // Get an instance of the Firestore database
        val db = FirebaseFirestore.getInstance()

        // Get a reference to the 'games' collection
        val gamesCollection = db.collection("games")

        // Get a reference to the specific game document
        val gameDocument = gamesCollection.document(gameId)

        // Update the timer in the game document
        gameDocument.update("config.timer", newTimer).await()
    }
}

fun handleRoundsToWinChange(newRoundsToWin: Int, gameId: String, scope: CoroutineScope) {
    scope.launch {
        // Get an instance of the Firestore database
        val db = FirebaseFirestore.getInstance()

        // Get a reference to the 'games' collection
        val gamesCollection = db.collection("games")

        // Get a reference to the specific game document
        val gameDocument = gamesCollection.document(gameId)

        // Update the roundsToWin in the game document
        gameDocument.update("config.roundsToWin", newRoundsToWin).await()
    }
}

fun handleDistortedModeChange(newDistortedMode: Boolean, gameId: String, scope: CoroutineScope) {
    scope.launch {
        // Get an instance of the Firestore database
        val db = FirebaseFirestore.getInstance()

        // Get a reference to the 'games' collection
        val gamesCollection = db.collection("games")

        // Get a reference to the specific game document
        val gameDocument = gamesCollection.document(gameId)

        // Update the distortedMode in the game document
        gameDocument.update("config.distortedMode", newDistortedMode).await()
    }
}

fun handleBoardTypeChange(newBoardTypeId: String, gameId: String, scope: CoroutineScope) {
    scope.launch {
        // Get an instance of the Firestore database
        val db = FirebaseFirestore.getInstance()

        // Get a reference to the 'games' collection
        val gamesCollection = db.collection("games")

        // Get a reference to the specific game document
        val gameDocument = gamesCollection.document(gameId)

        // Update the boardType in the game document
        gameDocument.update("config.currentBoardType.id", newBoardTypeId).await()
    }
}


fun updatePlayerScore(gameId: String, playerId: String, newScore: Int) {
    val gameRef = firestoreDB.collection("games").document(gameId)
    gameRef.update("$playerId.score", newScore)
}

fun setCurrentPlayer(gameId: String, currentPlayer: Player) {
    val gameRef = firestoreDB.collection("games").document(gameId)
    gameRef.update("currentPlayer", currentPlayer)
}

fun switchPlayer(gameId: String, currentPlayer: Player, player1: Player, player2: Player) {
    val nextPlayer = if (currentPlayer.id == player1.id) player2 else player1
    setCurrentPlayer(gameId, nextPlayer)
}

fun updatePositionInFirestore(gameId: String, position: String, mark: String) {
    val gameRef = firestoreDB.collection("games").document(gameId)
    gameRef.update("board.$position", mark)
}

fun resetBoardOnline(gameId: String, defaultBoard: Map<String, String>) {
    val gameRef = firestoreDB.collection("games").document(gameId)
    gameRef.update("board", defaultBoard)
}

fun setOnlineWinner(gameId: String, winner: Player?) {
    val gameRef = firestoreDB.collection("games").document(gameId)
    gameRef.update("winner", winner ?: null)
}

fun resetScoreOnline(gameId: String) {
    val gameRef = firestoreDB.collection("games").document(gameId)
    gameRef.update("player1.score", 0, "player2.score", 0)
}

fun updateTimer(gameId: String, countdown: Int) {
    val gameRef = firestoreDB.collection("games").document(gameId)
    gameRef.update("countdown", countdown)
}

fun updateTotalRounds(gameId: String, totalRounds: Int) {
    val gameRef = firestoreDB.collection("games").document(gameId)
    gameRef.update("totalRounds", totalRounds)
}

fun updatePauseStatus(gameId: String, pause: Boolean) {
    val gameRef = firestoreDB.collection("games").document(gameId)
    gameRef.update("pause", pause)
}

fun updateDraws(gameId: String, draws: Int) {
    val gameRef = firestoreDB.collection("games").document(gameId)
    gameRef.update("draws", draws)
}

fun updateBoardOpenedStatus(gameId: String, boardOpened: Boolean) {
    val gameRef = firestoreDB.collection("games").document(gameId)
    gameRef.update("boardOpened", boardOpened)
}

fun updateCountdown(gameId: String, countdown: Int) {
    val gameRef = firestoreDB.collection("games").document(gameId)
    gameRef.update("countdown", countdown)
}

fun increaseOtherPlayerOnlineScore(
    currentPlayer: Player,
    player1: Player,
    player2: Player,
    firestoreDB: FirebaseFirestore,
    gameId: String,
    scope: CoroutineScope,
    onSuccess: () -> Unit,
) {
    val gameRef = firestoreDB.collection("games").document(gameId)

    scope.launch {
        try {
            if (currentPlayer.id == player1.id) {
                val newScore = player2.score + 1
                gameRef.update("player2.score", newScore).await()
            } else {
                val newScore = player1.score + 1
                gameRef.update("player1.score", newScore).await()
            }
            onSuccess()
        } catch (e: Exception) {
            // Handle the exception here
            println("Error updating score: ${e.message}")
        }
    }
}

suspend fun updateHistory(userId: String, game: GameHistoryProps) {
    val gameHistoryRef = firestoreDB.collection("users").document(userId).collection("history")
    gameHistoryRef.add(game).await()
}

suspend fun updatePlayerStats(userId: String, won: Boolean) {
    val userRef = firestoreDB.collection("users").document(userId)

    if (won) {
        userRef.update(mapOf(
            "matches" to FieldValue.increment(1),
            "win" to FieldValue.increment(1)
        )).await()
    } else {
        userRef.update(mapOf(
            "matches" to FieldValue.increment(1),
            "loss" to FieldValue.increment(1)
        )).await()
    }
}

fun resetGameState(gameId: String, onlineGameData: OnlineGameData) {
    if (gameId.isNullOrEmpty()) return

    // Reset the board online
    resetBoardOnline(gameId, DEFAULT_BOARD)

    // Reset the score online
    resetScoreOnline(gameId)

    // Set the online winner to null
    setOnlineWinner(gameId, null)

    // Unpause the game
    updatePauseStatus(gameId, false)

    // Set the timer
    updateCountdown(gameId, onlineGameData.config.timer)

    // Update online draws to 0
    updateDraws(gameId, 0)

    // Update total rounds online to 0
    updateTotalRounds(gameId, 0)
}