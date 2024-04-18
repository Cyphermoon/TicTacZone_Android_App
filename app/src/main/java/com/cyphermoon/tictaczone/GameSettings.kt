package com.cyphermoon.tictaczone

data class BoardType(
    val dimension: String = DEFAULT_GAME_CONFIG.currentBoardType.dimension,
    val value: String = DEFAULT_GAME_CONFIG.currentBoardType.value,
    val id: String = DEFAULT_GAME_CONFIG.currentBoardType.id,
)

data class GameConfigType(
    val currentBoardType: BoardType = DEFAULT_GAME_CONFIG.currentBoardType,
    val timer: Int = DEFAULT_GAME_CONFIG.timer,
    val totalRounds: Int = DEFAULT_GAME_CONFIG.totalRounds,
    val roundsToWin: Int = DEFAULT_GAME_CONFIG.roundsToWin,
    val distortedMode: Boolean = DEFAULT_GAME_CONFIG.distortedMode,
    val revealMode: Boolean = DEFAULT_GAME_CONFIG.revealMode
)

data class AllGameBoardData(
    val boardType: List<BoardType>
)

val ALL_GAME_BOARD = AllGameBoardData(
    boardType = listOf(
        BoardType(
            dimension = "3",
            value = "3x3 Board",
            id = "3x3"
        ),
    )
)


val DEFAULT_GAME_CONFIG = GameConfigType(
    currentBoardType = BoardType(
        dimension = "3",
        value = "3x3 Board",
        id = "3x3"
    ),
    timer = 10,
    totalRounds = 3,
    roundsToWin = 2,
    distortedMode = false,
    revealMode = false
)

val DEFAULT_BOARD = mapOf(
    "1" to "",
    "2" to "",
    "3" to "",
    "4" to "",
    "5" to "",
    "6" to "",
    "7" to "",
    "8" to "",
    "9" to ""
)

sealed class GameModes(val mode: String){
    object Local : GameModes("Local")
    object Online : GameModes("Online")
    object AI: GameModes("AI")
}