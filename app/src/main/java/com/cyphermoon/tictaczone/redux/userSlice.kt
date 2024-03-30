package com.cyphermoon.tictaczone.redux

// Slice of the state related to the user
class userSlice {
    companion object {
        // Initial state for the user
        val initialState = PlayerProps(
            id = "",
            name = "",
            isAnonymous = true,
            matches = 0,
            win = 0,
            loss = 0,
            email = "",
            imageUrl = null,
            online = false
        )

        // Reducer function to handle actions related to the user and update the state
        fun reducer(state: PlayerProps = initialState, action: Any): PlayerProps {
            return when (action) {
                is userActions.updateUser -> action.user
                else -> state
            }
        }
    }
}

// Actions related to the user
sealed class userActions {
    data class updateUser(val user: PlayerProps)
}

// Data class to hold the properties of a user
data class PlayerProps(
    var id: String = "",
    var name: String = "",
    var isAnonymous: Boolean = true,
    var matches: Int = 0,
    var win: Int = 0,
    var loss: Int = 0,
    var email: String = "",
    var imageUrl: String? = null,
    var online: Boolean = false,
    val mark: String = "X",
    var localGameScore: Int = 0
)