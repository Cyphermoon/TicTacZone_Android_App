package com.cyphermoon.tictaczone.presentation.config_flow

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyphermoon.tictaczone.DEFAULT_GAME_CONFIG
import com.cyphermoon.tictaczone.ScreenRoutes
import com.cyphermoon.tictaczone.presentation.config_flow.composables.GameConfigBoard
import com.cyphermoon.tictaczone.presentation.config_flow.composables.VersusPlayerCard
import com.cyphermoon.tictaczone.presentation.config_flow.composables.ViewModeType
import com.cyphermoon.tictaczone.presentation.game_flow.utils.OnlineGameData
import com.cyphermoon.tictaczone.presentation.game_flow.utils.handleBoardTypeChange
import com.cyphermoon.tictaczone.presentation.game_flow.utils.handleDistortedModeChange
import com.cyphermoon.tictaczone.presentation.game_flow.utils.handleRoundsToWinChange
import com.cyphermoon.tictaczone.presentation.game_flow.utils.handleTimerChange
import com.cyphermoon.tictaczone.presentation.game_flow.utils.playerToGamePlayerProps
import com.cyphermoon.tictaczone.redux.LocalPlayersProps


import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

val gameDataFlow: MutableStateFlow<OnlineGameData?> = MutableStateFlow(null)

// Function to set up the real-time listener
fun setupRealTimeListener(gameId: String) {
    // Get an instance of the Firestore database
    val db = FirebaseFirestore.getInstance()

    // Get a reference to the 'games' collection
    val gamesCollection = db.collection("games")

    // Get a reference to the specific game document
    val gameDocument = gamesCollection.document(gameId)

    // Add a snapshot listener to the game document
    gameDocument.addSnapshotListener { snapshot, error ->
        if (error != null) {
            // Log the error
            println("Listen failed: $error")
            return@addSnapshotListener
        }

        if (snapshot != null && snapshot.exists()) {
            // Convert the snapshot to an OnlineGameData object
            val gameData = snapshot.toObject(OnlineGameData::class.java)

            // Post the gameData to the MutableStateFlow
            gameDataFlow.value = gameData
        } else {
            println("Current data: null")
        }
    }
}


@Composable
fun OnlineConfigScreen(navController: NavController, gameId: String?) {

    // Collect updates from the gameDataFlow
    val gameData by gameDataFlow.collectAsState()

    val scope = rememberCoroutineScope()

// Convert the Player objects to GamePlayerProps objects
    val players = gameData?.let {
        LocalPlayersProps(
            player1 = playerToGamePlayerProps(it.player1),
            player2 = playerToGamePlayerProps(it.player2)
        )
    }

    // Call setupRealTimeListener when the composable is first displayed
    LaunchedEffect(key1 = gameId) {
        gameId?.let { setupRealTimeListener(it) }
    }


    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // Display the VersusPlayerCard with the dummy data
        if (players != null) {
            VersusPlayerCard(players = players!!, startGame = {}, mode = ViewModeType.EDIT)
        } else {
            // Display a not found indicator
            Text(text = "One or both players has not been configured")

        }

        Spacer(modifier = Modifier.height(20.dp))

        GameConfigBoard(
            game = gameData?.config ?: DEFAULT_GAME_CONFIG,
            mode = ViewModeType.EDIT,
            onGameStart = { navController.navigate("${ScreenRoutes.OnlineGameScreen.route}/$gameId") },
            onTimerChange = { newTimer -> handleTimerChange(newTimer, gameId!!, scope) },
            onRoundsToWinChange = { newRoundsToWin -> handleRoundsToWinChange(newRoundsToWin, gameId!!, scope) },
            onDistortedModeChange = { newDistortedMode -> handleDistortedModeChange(newDistortedMode, gameId!!, scope) },
            onBoardTypeChange = { newBoardTypeId -> handleBoardTypeChange(newBoardTypeId, gameId!!, scope) }
        )

    }
}