package com.cyphermoon.tictaczone.presentation.auth_flow

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthStateViewModel: ViewModel() {
    private val _state = MutableStateFlow(AuthStateData())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResultData) {
        _state.update {it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.error
        ) }
    }

    fun resetState() {
        _state.update { AuthStateData() }
    }

}