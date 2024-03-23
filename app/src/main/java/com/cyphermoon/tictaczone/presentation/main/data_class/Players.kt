package com.cyphermoon.tictaczone.presentation.main.data_class

//data class PlayerCardProps(
//    val id: String,
//    val avatar: UserAvatarProps,
//    val matches: Int,
//    val wins: Int,
//    val loss: Int,
//    val online: Boolean? = null
//)
//
//data class SelectedPlayerType(
//    val id: String,
//    val avatar: UserAvatarProps,
//    val matches: Int,
//    val wins: Int,
//    val loss: Int,
//    val online: Boolean? = null
//)

data class Player(
    var name: String
)

data class LocalPlayersProps(
    var player2: Player
)

data class PlayerProps(
    val id: String,
    val name: String,
    val isAnonymous: Boolean,
    val matches: Int,
    val win: Int,
    val loss: Int,
    val email: String,
    val imageUrl: String?,
    val online: Boolean
)

data class OnlinePlayerProps(
    val id: String,
    val name: String,
    val isAnonymous: Boolean,
    val matches: Int,
    val win: Int,
    val loss: Int,
    val email: String,
    val imageUrl: String?,
    val online: Boolean,
    val score: Int,
    val mark: String,
    val view: String
)