package com.cyphermoon.tictaczone.presentation.game_flow.utils
import com.cyphermoon.tictaczone.DEFAULT_GAME_CONFIG
import com.cyphermoon.tictaczone.GameConfigType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

data class OnlineGameData(
    val board: Map<String, String> = emptyMap(),
    val boardOpened: Boolean = false,
    val config: GameConfigType = GameConfigType(), // replace with actual default value
    val countdown: Int = 0,
    val createdAt: Timestamp? = null,
    val currentPlayer: Player = Player(),
    val draws: Int = 0,
    val initiatingPlayerId: String = "",
    val isDraw: Boolean = false,
    val pause: Boolean = false,
    val player1: Player = Player(),
    val player2: Player = Player(),
    val totalRounds: Int = 0,
    val winner: Player? = null
)


data class Player(
    val id: String = "",
    val mark: String = "",
    val name: String = "",
    val online: Boolean = false,
    val photoURL: String? = null,
    val score: Int = 0,
    val view: String = ""
)

data class GameHistoryProps(
    val opponent: String,
    val gameType: String,
    val firstToWin: Int,
    val result: String
)