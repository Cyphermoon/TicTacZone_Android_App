package com.cyphermoon.tictaczone

data class BoardType(
    val dimension: String,
    val value: String,
    val id: String
)

data class GameConfigType(
    val currentBoardType: BoardType,
    val timer: Int,
    val totalRounds: Int,
    val roundsToWin: Int,
    val distortedMode: Boolean,
    val revealMode: Boolean
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

sealed class GameModes(val mode: String){
    object Local : GameModes("Local")
    object Online : GameModes("Online")
    object AI: GameModes("AI")
}