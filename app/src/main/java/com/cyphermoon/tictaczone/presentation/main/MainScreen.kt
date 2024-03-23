package com.cyphermoon.tictaczone.presentation.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyphermoon.tictaczone.presentation.main.components.Logo
import com.cyphermoon.tictaczone.presentation.main.components.ProfileStatsCard
import com.cyphermoon.tictaczone.presentation.auth_flow.FirebaseUserData
import com.cyphermoon.tictaczone.presentation.auth_flow.GoogleAuthenticator
import com.cyphermoon.tictaczone.presentation.main.components.LocalOption


@Composable
fun MainScreen(navController: NavController, userData: FirebaseUserData?) {
    val googleAuthUIClient = GoogleAuthenticator(context = LocalContext.current)
    val coroutineScope = rememberCoroutineScope()

    // A logout button
    //  Button(onClick = {
    //    coroutineScope.launch {
    //      googleAuthUIClient.signOut()
    //      navController.navigate(ScreenRoutes.LoginScreen.route)
    //    }
    //  }) {
    //    Text(text = "Logout")
    //  }
    Column(
        modifier=
        Modifier
            .fillMaxHeight()
            .padding(5.dp)
    ) {
        Logo(name = null)
        Spacer(modifier = Modifier.height(10.dp))
        ProfileStatsCard(
            matches = 10,
            name = "John Doe",
            win = 5,
            loss = 4,
            handleChallenge = null,
            online = true,
            id = "adamq"
        )
        Spacer(modifier = Modifier.height(10.dp))

        LocalOption(handleLocalPlayerStart = null)
    }
}
