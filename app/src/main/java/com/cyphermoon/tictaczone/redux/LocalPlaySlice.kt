package com.cyphermoon.tictaczone.redux

class LocalPlaySlice {
    companion object {
        val initialState =  LocalPlayConfig()

        fun reducer(state: LocalPlayConfig = initialState, action: Any): LocalPlayConfig {
            return when (action) {
                is localPlayActions.updatePlayer1 -> state.copy(players = LocalPlayersProps(player1 = action.player, player2 = state.players?.player2))
                is localPlayActions.updatePlayer2 -> state.copy(players = LocalPlayersProps(player1 = state.players?.player1, player2 = action.player))
                is localPlayActions.updatePlayers -> state.copy(players = action.players)
                else -> state
            }
        }
    }
}





// Data Class

data class LocalPlayConfig (
    val players: LocalPlayersProps? = LocalPlayersProps(
        player1 = PlayerProps(),
        player2 = Player()
    )
)

data class Player(
    var name: String = "",
    var id: String = "",
    var mark: String = "O"
)

data class LocalPlayersProps(
    var player1: PlayerProps?,
    var player2: Player?
)


// Actions related to local play config
sealed class localPlayActions {
    data class updatePlayer1(val player: PlayerProps)
    data class updatePlayer2(val player: Player)
    data class updatePlayers(val players: LocalPlayersProps)
}


