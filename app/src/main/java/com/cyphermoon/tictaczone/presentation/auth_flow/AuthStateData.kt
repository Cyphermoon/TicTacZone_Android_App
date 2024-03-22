package com.cyphermoon.tictaczone.presentation.auth_flow

data class AuthStateData(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
