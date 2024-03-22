package com.cyphermoon.tictaczone.presentation

import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.cyphermoon.tictaczone.ScreenRoutes
import com.cyphermoon.tictaczone.presentation.auth_flow.AuthStateData
import com.cyphermoon.tictaczone.presentation.auth_flow.GoogleAuthenticator
import kotlinx.coroutines.launch


@Composable
fun MainScreen(navController: NavController){
  val googleAuthUIClient = GoogleAuthenticator(context = LocalContext.current)
  val coroutineScope = rememberCoroutineScope()

  Text("Main Screen. Let's code some Tic Tac Toe!")
  // A logout button
  Button(onClick = {
    coroutineScope.launch {
      googleAuthUIClient.signOut()
      navController.navigate(ScreenRoutes.LoginScreen.route)
    }
  }) {
    Text(text = "Logout")
  }
}
