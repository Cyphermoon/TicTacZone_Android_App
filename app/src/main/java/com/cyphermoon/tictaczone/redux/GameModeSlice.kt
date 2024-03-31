package com.cyphermoon.tictaczone.redux

// This data class represents the game mode in the application.
data class GameMode(
    val gameMode: String? = null
)

// This class contains the initial state and reducer function for the game mode slice of the state.
class GameModeSlice {
    companion object {
        // The initial state for the game mode. By default, the game mode is null.
        val initialState = GameMode()

        // The reducer function for the game mode. It takes the current state and an action,
        // and returns the new state based on the action.
        fun reducer(state: GameMode = initialState, action: Any): GameMode {
            return when (action) {
                // If the action is UpdateGameMode, the new state is the game mode contained in the action.
                is GameModeActions.UpdateGameMode -> action.gameMode
                // For any other action, the state remains unchanged.
                else -> state
            }
        }
    }
}

// This sealed class represents the actions that can be performed on the game mode.
sealed class GameModeActions {
    // This data class represents an action to update the game mode. It contains the new game mode.
    data class UpdateGameMode(val gameMode: GameMode) : GameModeActions()
}