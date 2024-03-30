package com.cyphermoon.tictaczone

import android.app.Activity.RESULT_OK
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cyphermoon.tictaczone.data.UserRepository
import com.cyphermoon.tictaczone.presentation.auth_flow.LoginScreen
import com.cyphermoon.tictaczone.presentation.main.MainScreen
import com.cyphermoon.tictaczone.presentation.auth_flow.AuthStateViewModel
import com.cyphermoon.tictaczone.presentation.auth_flow.GoogleAuthenticator
import com.cyphermoon.tictaczone.presentation.config_flow.ConfigScreen
import com.cyphermoon.tictaczone.presentation.game_flow.GameScreen
import com.cyphermoon.tictaczone.redux.store
import com.cyphermoon.tictaczone.redux.userActions
import kotlinx.coroutines.launch

@Composable
fun AppNavigator() {
    // Create a NavController, which keeps track of the current navigation state
    val navController = rememberNavController()

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

    // The NavHost is a composable that contains all of the destinations in this app
    // It takes in the NavController and the route of the start destination
    NavHost(navController = navController, startDestination = ScreenRoutes.LoginScreen.route) {

        // A composable function for the MainScreen
        // When the route matches ScreenRoutes.MainScreen.route, the MainScreen composable will be displayed
        composable(ScreenRoutes.MainScreen.route) {
            MainScreen(navController = navController, userData=googleAuthUiClient.getSignedInUser())
        }
        // A composable function for Config Screen
        composable(ScreenRoutes.ConfigScreen.route){
            ConfigScreen(navController = navController)
        }
        // A composable function for Game Screen
        composable(ScreenRoutes.GameScreen.route){
            GameScreen()
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
                state=state,
                onSignClicked={
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