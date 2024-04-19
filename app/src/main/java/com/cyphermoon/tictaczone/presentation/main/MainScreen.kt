package com.cyphermoon.tictaczone.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cyphermoon.tictaczone.GameModes
import com.cyphermoon.tictaczone.ScreenRoutes
import com.cyphermoon.tictaczone.presentation.main.components.Logo
import com.cyphermoon.tictaczone.presentation.main.components.ProfileStatsCard
import com.cyphermoon.tictaczone.presentation.auth_flow.FirebaseUserData
import com.cyphermoon.tictaczone.presentation.auth_flow.GoogleAuthenticator
import com.cyphermoon.tictaczone.presentation.main.components.LocalOption
import com.cyphermoon.tictaczone.redux.GameMode
import com.cyphermoon.tictaczone.redux.GameModeActions
import com.cyphermoon.tictaczone.redux.GamePlayerProps
import com.cyphermoon.tictaczone.redux.LocalPlayersProps
import com.cyphermoon.tictaczone.redux.Player
import com.cyphermoon.tictaczone.redux.LocalPlayActions
import com.cyphermoon.tictaczone.redux.store
import kotlinx.coroutines.launch


// A logout button
//  Button(onClick = {
//    coroutineScope.launch {
//      googleAuthUIClient.signOut()
//      navController.navigate(ScreenRoutes.LoginScreen.route)
//    }
//  }) {
//    Text(text = "Logout")
//  }

@Composable
fun MainScreen(navController: NavController, userData: FirebaseUserData?) {
    val googleAuthUIClient = GoogleAuthenticator(context = LocalContext.current)
    val coroutineScope = rememberCoroutineScope()


    var userState by remember { mutableStateOf(store.getState().user) }

    // This function is used to handle the configuration update for local player.
// It takes a Player object as input, which represents the second player in the game.
    fun handleLocalPlayerUpdateConfig(player2: Player): Unit {
        // Create a GamePlayerProps object for the first player.
        // The ID and name are taken from the current user state.
        // The mark is set to "X" and the score is initialized to 0.
        val player1 = GamePlayerProps(
            id = userState.id,
            name = userState.name,
            mark = "X",
            score = 0,
        )

        // Dispatch an action to update the players in the local play configuration.
        // The players are set to the newly created player1 and the input player2.
        store.dispatch(
            LocalPlayActions.UpdatePlayers(
                LocalPlayersProps(
                    player1 = player1,
                    player2 = GamePlayerProps(
                        id = player2.id,
                        name = player2.name,
                        mark = "O",
                        score = 0,
                    )
                )
            )
        )

        // Dispatch an action to update the current player in the local play configuration.
        // The current player is set to player1.
        store.dispatch(LocalPlayActions.UpdateCurrentPlayer(player1))

        // Dispatch an action to update the game mode in the game mode configuration.
        // The game mode is set to Local.
        store.dispatch(GameModeActions.UpdateGameMode(GameMode(mode = GameModes.Local.mode)))

        // Navigate to the ConfigScreen route.
        navController.navigate(ScreenRoutes.ConfigScreen.route)
    }

    //Subscribe to the store
    DisposableEffect(key1 = store.getState().user) {
        val unsubscribe = store.subscribe {
            userState = store.getState().user
        }

        onDispose {
            // Unsubscribe when the composable is disposed
            unsubscribe()
        }
    }

    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {

                Logo(name = null)
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    modifier = Modifier
                        .clickable(onClick = {
                            navController.navigate(ScreenRoutes.GameHistoryScreen.route)
                        }),
                    text = "Game History",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textDecoration = TextDecoration.Underline,

                    )
            }

            //A logout button
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                onClick = {
                    coroutineScope.launch {
                        googleAuthUIClient.signOut()
                        navController.navigate(ScreenRoutes.LoginScreen.route)
                    }
                }) {
                Text(text = "Logout")
            }

        }

        Spacer(modifier = Modifier.height(10.dp))

        ProfileStatsCard(
            matches = userState.matches,
            name = userState.name,
            win = userState.win,
            loss = userState.loss,
            handleChallenge = null,
            online = true,
            id = userState.id
        )
        Spacer(modifier = Modifier.height(26.dp))

        LocalOption { player -> handleLocalPlayerUpdateConfig(player) }
    }
}
