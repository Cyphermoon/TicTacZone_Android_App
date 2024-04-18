package com.cyphermoon.tictaczone.redux

import com.cyphermoon.tictaczone.BoardType
import com.cyphermoon.tictaczone.DEFAULT_BOARD
import com.cyphermoon.tictaczone.DEFAULT_GAME_CONFIG
import com.cyphermoon.tictaczone.GameConfigType

class LocalPlaySlice {
    companion object {
        val initialState = LocalPlayConfig()

fun localPlayReducer(state: LocalPlayConfig = initialState, action: Any): LocalPlayConfig {
    return state.let {
        when (action) {
            // Player related actions
            is LocalPlayActions.UpdatePlayer1 -> it.copy(players = it.players?.copy(player1 = action.player))
            is LocalPlayActions.UpdatePlayer2 -> it.copy(players = it.players?.copy(player2 = action.player))
            is LocalPlayActions.UpdatePlayers -> it.copy(players = action.players)
            is LocalPlayActions.UpdateCurrentPlayer -> it.copy(currentPlayer = action.player)

            // Game configuration related actions
            is LocalPlayActions.UpdateCurrentBoardType -> it.copy(gameConfig = it.gameConfig?.copy(currentBoardType = action.boardType))
            is LocalPlayActions.UpdateTimer -> it.copy(gameConfig = it.gameConfig?.copy(timer = action.timer))
            is LocalPlayActions.UpdateTotalRounds -> it.copy(gameConfig = it.gameConfig?.copy(totalRounds = action.totalRounds))
            is LocalPlayActions.UpdateRoundsToWin -> it.copy(gameConfig = it.gameConfig?.copy(roundsToWin = action.roundsToWin))
            is LocalPlayActions.UpdateDistortedMode -> it.copy(gameConfig = it.gameConfig?.copy(distortedMode = action.distortedMode))
            is LocalPlayActions.UpdateRevealMode -> it.copy(gameConfig = it.gameConfig?.copy(revealMode = action.revealMode))
            is LocalPlayActions.UpdateGameConfig -> it.copy(gameConfig = action.gameConfig)

            // Game state related actions
            is LocalPlayActions.UpdateCountdown -> it.copy(countdown = action.countdown)
            is LocalPlayActions.UpdateBoard -> it.copy(board = action.board)
            is LocalPlayActions.UpdateDraws -> it.copy(draws = action.draws)
            is LocalPlayActions.UpdateCurrentRound -> it.copy(currentRound = action.currentRound)

            else -> it
        }
    }
}

    }
}


// Data Class

data class LocalPlayConfig(
    val players: LocalPlayersProps? = LocalPlayersProps(
        player1 = GamePlayerProps(),
        player2 = GamePlayerProps()
    ),
    val gameConfig: GameConfigType? = DEFAULT_GAME_CONFIG,
    var currentPlayer: GamePlayerProps? = null,
    var countdown: Int = DEFAULT_GAME_CONFIG.timer,

    var board: Map<String, String> =  DEFAULT_BOARD,
    var draws: Int = 0,
    var currentRound: Int = 1
)

data class Player(
    var name: String = "",
    var id: String = "",
    var mark: String = "O",
    var localGameScore: Int = 0
)

data class GamePlayerProps(
    val name: String? = null,
    val id: String? = null,
    val mark: String? = null,
    var score: Int = 0,
    val difficulty: String? = null
)

data class LocalPlayersProps(
    var player1: GamePlayerProps?,
    var player2: GamePlayerProps?
)


// Actions related to local play config
// Actions related to local play config
sealed class LocalPlayActions {
    // Player related actions
    data class UpdatePlayer1(val player: GamePlayerProps) : LocalPlayActions()
    data class UpdatePlayer2(val player: GamePlayerProps) : LocalPlayActions()
    data class UpdatePlayers(val players: LocalPlayersProps) : LocalPlayActions()
    data class UpdateCurrentPlayer(val player: GamePlayerProps) : LocalPlayActions()

    // Game configuration related actions
    data class UpdateCurrentBoardType(val boardType: BoardType) : LocalPlayActions()
    data class UpdateTimer(val timer: Int) : LocalPlayActions()
    data class UpdateTotalRounds(val totalRounds: Int) : LocalPlayActions()
    data class UpdateRoundsToWin(val roundsToWin: Int) : LocalPlayActions()
    data class UpdateDistortedMode(val distortedMode: Boolean) : LocalPlayActions()
    data class UpdateRevealMode(val revealMode: Boolean) : LocalPlayActions()
    data class UpdateGameConfig(val gameConfig: GameConfigType) : LocalPlayActions()

    // Game state related actions
    data class UpdateCountdown(val countdown: Int) : LocalPlayActions()
    data class UpdateBoard(val board: Map<String, String>) : LocalPlayActions()
    data class UpdateDraws(val draws: Int) : LocalPlayActions()
    data class UpdateCurrentRound(val currentRound: Int) : LocalPlayActions()
}

