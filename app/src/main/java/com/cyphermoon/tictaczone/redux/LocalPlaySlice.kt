package com.cyphermoon.tictaczone.redux

import com.cyphermoon.tictaczone.BoardType
import com.cyphermoon.tictaczone.DEFAULT_GAME_CONFIG
import com.cyphermoon.tictaczone.GameConfigType

class LocalPlaySlice {
    companion object {
        val initialState = LocalPlayConfig()
   fun localPlayReducer(state: LocalPlayConfig = initialState, action: Any): LocalPlayConfig {
    return state.let {
        when (action) {
            is LocalPlayActions.UpdatePlayer1 -> it.copy(players = it.players?.copy(player1 = action.player))
            is LocalPlayActions.UpdatePlayer2 -> it.copy(players = it.players?.copy(player2 = action.player))
            is LocalPlayActions.UpdatePlayers -> it.copy(players = action.players)

            is LocalPlayActions.UpdateCurrentBoardType -> it.copy(gameConfig = it.gameConfig?.copy(currentBoardType = action.boardType))
            is LocalPlayActions.UpdateTimer -> it.copy(gameConfig = it.gameConfig?.copy(timer = action.timer))
            is LocalPlayActions.UpdateTotalRounds -> it.copy(gameConfig = it.gameConfig?.copy(totalRounds = action.totalRounds))
            is LocalPlayActions.UpdateRoundsToWin -> it.copy(gameConfig = it.gameConfig?.copy(roundsToWin = action.roundsToWin))
            is LocalPlayActions.UpdateDistortedMode -> it.copy(gameConfig = it.gameConfig?.copy(distortedMode = action.distortedMode))
            is LocalPlayActions.UpdateRevealMode -> it.copy(gameConfig = it.gameConfig?.copy(revealMode = action.revealMode))
            is LocalPlayActions.UpdateGameConfig -> it.copy(gameConfig = action.gameConfig)

            else -> it
        }
    }
}

    }
}


// Data Class

data class LocalPlayConfig(
    val players: LocalPlayersProps? = LocalPlayersProps(
        player1 = PlayerProps(),
        player2 = Player()
    ),
    val gameConfig: GameConfigType? = DEFAULT_GAME_CONFIG
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
sealed class LocalPlayActions {
    data class UpdatePlayer1(val player: PlayerProps) : LocalPlayActions()
    data class UpdatePlayer2(val player: Player) : LocalPlayActions()
    data class UpdatePlayers(val players: LocalPlayersProps) : LocalPlayActions()

    // Actions for updating GameConfigType
    data class UpdateCurrentBoardType(val boardType: BoardType) : LocalPlayActions()
    data class UpdateTimer(val timer: Int) : LocalPlayActions()
    data class UpdateTotalRounds(val totalRounds: Int) : LocalPlayActions()
    data class UpdateRoundsToWin(val roundsToWin: Int) : LocalPlayActions()
    data class UpdateDistortedMode(val distortedMode: Boolean) : LocalPlayActions()
    data class UpdateRevealMode(val revealMode: Boolean) : LocalPlayActions()

    // Action for updating the entire GameConfigType
    data class UpdateGameConfig(val gameConfig: GameConfigType) : LocalPlayActions()
}

