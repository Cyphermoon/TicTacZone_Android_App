package com.cyphermoon.tictaczone

sealed class ScreenRoutes (val route: String) {
    object MainScreen: ScreenRoutes("main_screen")
    object ConfigScreen: ScreenRoutes("config_screen")
    object LoginScreen: ScreenRoutes("login_screen")
}