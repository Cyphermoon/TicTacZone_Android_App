package com.cyphermoon.tictaczone

import androidx.compose.ui.graphics.vector.ImageVector

sealed class ScreenRoutes (val route: String) {
    object MainScreen: ScreenRoutes("main_screen")
    object ConfigScreen: ScreenRoutes("config_screen")
    object LoginScreen: ScreenRoutes("login_screen")

    object GameScreen: ScreenRoutes("game_screen")

    object AIGameScreen: ScreenRoutes("ai_game_screen")
    object AICharacters: ScreenRoutes("ai_characters")

    object OnlineGameScreen: ScreenRoutes("online_game_screen")
    object OnlineGamePlayersScreen: ScreenRoutes("online_game_players_screen")
    object OnlineGameConfigGameScreen: ScreenRoutes("online_game_config_screen")
}
