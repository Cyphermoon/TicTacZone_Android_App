package com.cyphermoon.tictaczone.presentation.auth_flow


data class SignInResultData(
    val data: FirebaseUserData?,
    val error: String?
)

data class FirebaseUserData(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)
