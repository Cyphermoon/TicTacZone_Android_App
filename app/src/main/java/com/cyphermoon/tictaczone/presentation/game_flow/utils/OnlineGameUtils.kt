package com.cyphermoon.tictaczone.presentation.game_flow.utils

import com.cyphermoon.tictaczone.redux.GamePlayerProps
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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