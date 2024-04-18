package com.cyphermoon.tictaczone.presentation.game_flow

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyphermoon.tictaczone.DEFAULT_GAME_CONFIG
import com.cyphermoon.tictaczone.ScreenRoutes
import com.cyphermoon.tictaczone.presentation.game_flow.utils.OnlineGameData
import com.cyphermoon.tictaczone.presentation.game_flow.utils.Player
import com.cyphermoon.tictaczone.presentation.game_flow.utils.updateOrGetGame
import com.cyphermoon.tictaczone.presentation.main.components.Logo
import com.cyphermoon.tictaczone.presentation.main.components.ProfileStatsCard
import com.cyphermoon.tictaczone.redux.GameMode
import com.cyphermoon.tictaczone.redux.GameModeActions
import com.cyphermoon.tictaczone.redux.PlayerProps
import com.cyphermoon.tictaczone.redux.store
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


// This is a suspending function that fetches all users from the Firestore database
suspend fun fetchAllUsers(currentId: String): List<PlayerProps> {
    // Get an instance of the Firestore database
    val db = FirebaseFirestore.getInstance()

    // Get a reference to the 'users' collection in the database
    val usersCollection = db.collection("users")

    // Fetch all documents in the 'users' collection except the current user
    // The 'await' function is used to wait for the operation to complete
    val usersSnapshot = usersCollection.whereNotEqualTo("id", currentId).get().await()

    // Create a mutable list to hold the fetched users
    val usersList = mutableListOf<PlayerProps>()

    // Iterate over each document in the fetched documents
    for (document in usersSnapshot.documents) {
        // Convert the document into a 'PlayerProps' object
        val user = document.toObject(PlayerProps::class.java)

        // If the conversion was successful (i.e., the document was not null), add the user to the list
        if (user != null) {
            usersList.add(user)
        }
    }

    // Return the list of users
    return usersList
}

@Composable
fun OnlineGamePlayersScreen(navController: NavController) {
    var usersList by remember { mutableStateOf(listOf<PlayerProps>()) }
    var currentPlayer by remember { mutableStateOf(store.getState().user) }
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        usersList = fetchAllUsers(currentPlayer.id)
    }

    fun handleChallenge(selectedPlayer: PlayerProps) {
        coroutineScope.launch {
            // create or get the current game
            // update it locally
            val gameId =
                listOf(currentPlayer.id, selectedPlayer.id).sorted().joinToString("-") + "-game"
            val gameData = OnlineGameData(
                player1 = Player(
                    id = currentPlayer.id,
                    name = currentPlayer.name,
                    mark = "x",
                    photoURL = currentPlayer.imageUrl,
                    score = 0,
                    online = currentPlayer.online,
                    view = "edit"
                ),
                player2 = Player(
                    id = selectedPlayer.id,
                    name = selectedPlayer.name,
                    mark = "o",
                    photoURL = selectedPlayer.imageUrl,
                    score = 0,
                    online = selectedPlayer.online,
                    view = "view"
                ),
                board = mapOf(
                    "1" to "",
                    "2" to "",
                    "3" to "",
                    "4" to "",
                    "5" to "",
                    "6" to "",
                    "7" to "",
                    "8" to "",
                    "9" to ""
                ),
                config = DEFAULT_GAME_CONFIG,
                currentPlayer = Player(
                    id = currentPlayer.id,
                    name = currentPlayer.name,
                    mark = "x",
                    photoURL = currentPlayer.imageUrl,
                    score = 0,
                    online = currentPlayer.online,
                    view = "edit"
                ),
                boardOpened = true,
                isDraw = false,
                initiatingPlayerId = currentPlayer.id,
                pause = false,
                countdown = DEFAULT_GAME_CONFIG.timer,
                draws = 0,
                totalRounds = 0,
                winner = null,
                createdAt = Timestamp.now()
            )

            try {
                updateOrGetGame(gameId, gameData)
                GameModeActions.UpdateGameMode(GameMode(mode = "online"))
                navController.navigate("${ScreenRoutes.OnlineGameConfigGameScreen.route}/$gameId")
            } catch (err: Exception) {
                println("Error: $err")
            }
        }
    }



    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Logo(name = "All Players")
        Spacer(modifier = Modifier.height(10.dp))
        // Display placeholders while the data is being loaded
        if (usersList.isEmpty()) {
            repeat(5) {
                ProfileStatsCardPlaceholder()
                Spacer(modifier = Modifier.height(18.dp))
            }
        } else {
            usersList.forEach { user ->
                ProfileStatsCard(
                    matches = user.matches,
                    name = user.name,
                    win = user.win,
                    loss = user.loss,
                    handleChallenge = {
                        handleChallenge(user)
                    },
                    online = user.online,
                    id = user.id
                )
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

    }
}

@Composable
fun ProfileStatsCardPlaceholder() {
    // Display a placeholder with a fixed size
    Box(modifier = Modifier.size(200.dp, 100.dp)) {
        // ...
    }
}