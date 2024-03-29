package com.cyphermoon.tictaczone.redux

data class GamePlayerProps(
    val name: String,
    val id: String,
    val mark: String,
    var score: Int,
    val difficulty: String? = null
)