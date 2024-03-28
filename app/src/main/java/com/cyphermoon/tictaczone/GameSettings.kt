package com.cyphermoon.tictaczone

data class BoardType(
    val dimension: String,
    val value: String,
    val id: String
)

data class GameConfigType(
    val boardType: List<BoardType>,
    val currentBoardType: BoardType,
    val timer: Int,
    val totalRounds: Int,
    val roundsToWin: Int,
    val distortedMode: Boolean,
    val revealMode: Boolean
)
val DEFAULT_GAME_CONFIG = GameConfigType(
    boardType = listOf(
        BoardType(
            dimension = "3",
            value = "3x3 Board",
            id = "3x3"
        )
        // Add more board types here if needed
    ),
    currentBoardType = BoardType(
        dimension = "3",
        value = "3x3 Board",
        id = "3x3"
    ),
    timer = 10,
    totalRounds = 3,
    roundsToWin = 2,
    distortedMode = true,
    revealMode = false
)