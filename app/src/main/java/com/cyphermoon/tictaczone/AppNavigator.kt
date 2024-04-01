package com.cyphermoon.tictaczone

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cyphermoon.tictaczone.data.UserRepository
import com.cyphermoon.tictaczone.presentation.auth_flow.LoginScreen
import com.cyphermoon.tictaczone.presentation.main.MainScreen
import com.cyphermoon.tictaczone.presentation.auth_flow.AuthStateViewModel
import com.cyphermoon.tictaczone.presentation.auth_flow.GoogleAuthenticator
import com.cyphermoon.tictaczone.presentation.config_flow.ConfigScreen
import com.cyphermoon.tictaczone.presentation.game_flow.AICharacters
import com.cyphermoon.tictaczone.presentation.game_flow.LocalGameScreen
import com.cyphermoon.tictaczone.presentation.game_flow.OnlineGameScreen
import com.cyphermoon.tictaczone.presentation.game_flow.utils.resetAfterFullRound
import com.cyphermoon.tictaczone.presentation.game_flow.utils.resetBoard
import com.cyphermoon.tictaczone.redux.LocalPlayActions
import com.cyphermoon.tictaczone.redux.store
import com.cyphermoon.tictaczone.redux.userActions
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigator() {
    // Create a NavController, which keeps track of the current navigation state
    val navController = rememberNavController()
    var selectedTab by rememberSaveable { mutableStateOf(0) }

    var gameMode by remember { mutableStateOf(store.getState().gameMode.mode) }
    var localPlay by remember { mutableStateOf(store.getState().localPlay) }


    // Get the application context
    val applicationContext = LocalContext.current

    // Create an instance of the GoogleAuthenticator class
    val googleAuthUiClient by lazy {
        GoogleAuthenticator(context = applicationContext)
    }

    val coroutineScope = rememberCoroutineScope()

    val userRepository = UserRepository()

    // get custom user profile from firestore using signed in Id
    LaunchedEffect(key1 = Unit) {
        val user = googleAuthUiClient.getSignedInUser()
        if (user != null) {
            userRepository.listenForUserUpdates(userId = user.uid) { userProps ->
                Log.v("UserProps", userProps.toString())

                if (userProps != null) {
                    store.dispatch(userActions.updateUser(userProps))
                }
            }
        }
    }

    DisposableEffect(key1 = store) {
    val unsubscribe = store.subscribe {
        // Update localPlay state whenever the state in the store changes
        localPlay = store.getState().localPlay
    }

    onDispose {
        // Unsubscribe when the composable is disposed
        unsubscribe()
    }
}


    // Reset the board if player 2 or the game mode changes
    LaunchedEffect(
        key1 = gameMode,
        key2 = localPlay.players?.player2?.difficulty
    ){

        // Retrieve the player1 and player2 from the localPlay state
        val player1 = localPlay.players?.player1
        val player2 = localPlay.players?.player2

        resetAfterFullRound(localPlay.gameConfig?.timer, player1, player2)

        // Reset the board
        store.dispatch(LocalPlayActions.UpdateBoard(resetBoard()))
    }

    Scaffold(
        bottomBar = {
            BottomTabBarNavigation(
                selectedIdx = selectedTab,
                onTabSelected = { idx ->
                    selectedTab = idx
                },
                navController = navController
            )
        }
    ) {paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // The NavHost is a composable that contains all of the destinations in this app
            // It takes in the NavController and the route of the start destination
            NavHost(
                navController = navController,
                startDestination = ScreenRoutes.LoginScreen.route
            ) {

                // A composable function for the MainScreen
                // When the route matches ScreenRoutes.MainScreen.route, the MainScreen composable will be displayed
                composable(ScreenRoutes.MainScreen.route) {
                    MainScreen(
                        navController = navController,
                        userData = googleAuthUiClient.getSignedInUser()
                    )
                }
                // A composable function for Config Screen
                composable(ScreenRoutes.ConfigScreen.route) {
                    ConfigScreen(navController = navController)
                }
                // A composable function for Game Screen
                composable(ScreenRoutes.GameScreen.route) {
                    LocalGameScreen()
                }
                // A composable function for AI Characters Screen
                composable(ScreenRoutes.AICharacters.route) {
                    AICharacters(navController = navController)
                }

                // A composable function for Online Game Screen
                composable(ScreenRoutes.OnlineGameScreen.route) {
                    OnlineGameScreen()
                }
                composable(ScreenRoutes.LoginScreen.route) {
                    val viewModel = viewModel<AuthStateViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    //navigate to the login screen if the user is not signed in
                    LaunchedEffect(key1 = Unit) {
                        if (googleAuthUiClient.getSignedInUser() != null) {
                            navController.navigate(ScreenRoutes.MainScreen.route)
                        }
                    }

                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                        onResult = { result ->
                            if (result.resultCode == RESULT_OK) {
                                coroutineScope.launch {
                                    val signInResult = googleAuthUiClient.signInWithIntent(
                                        intent = result.data ?: return@launch
                                    )
                                    viewModel.onSignInResult(signInResult)
                                }
                            }
                        }
                    )

                    // Check if the user has signed in successfully
                    LaunchedEffect(key1 = state.isSignInSuccessful) {
                        if (state.isSignInSuccessful) {
                            Toast.makeText(
                                applicationContext,
                                "Sign in successful",
                                Toast.LENGTH_LONG
                            ).show()

                            navController.navigate(ScreenRoutes.MainScreen.route)
                            viewModel.resetState()
                        }
                    }

                    LoginScreen(
                        navController = navController,
                        state = state,
                        onSignClicked = {
                            coroutineScope.launch {
                                val signInIntentSender = googleAuthUiClient.signInWithGoogle()
                                launcher.launch(
                                    IntentSenderRequest.Builder(
                                        signInIntentSender ?: return@launch
                                    ).build()
                                )
                            }
                        }
                    )
                }

            }
        }

    }

}