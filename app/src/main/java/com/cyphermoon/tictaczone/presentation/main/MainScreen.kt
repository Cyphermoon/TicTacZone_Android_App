package com.cyphermoon.tictaczone.presentation.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyphermoon.tictaczone.ScreenRoutes
import com.cyphermoon.tictaczone.presentation.main.components.Logo
import com.cyphermoon.tictaczone.presentation.main.components.ProfileStatsCard
import com.cyphermoon.tictaczone.presentation.auth_flow.FirebaseUserData
import com.cyphermoon.tictaczone.presentation.auth_flow.GoogleAuthenticator
import com.cyphermoon.tictaczone.presentation.main.components.LocalOption
import com.cyphermoon.tictaczone.redux.GamePlayerProps
import com.cyphermoon.tictaczone.redux.LocalPlayersProps
import com.cyphermoon.tictaczone.redux.Player
import com.cyphermoon.tictaczone.redux.LocalPlayActions
import com.cyphermoon.tictaczone.redux.store


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

    fun handleLocalPlayerUpdateConfig(player2: Player): Unit {
        val player1 = GamePlayerProps(
            id = userState.id,
            name = userState.name,
            mark = "X",
            score = 0,
        )
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

        store.dispatch(LocalPlayActions.UpdateCurrentPlayer(player1))
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
    ) {
        Logo(name = null)

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
