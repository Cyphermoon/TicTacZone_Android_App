package com.cyphermoon.tictaczone.redux

import android.util.Log
import org.reduxkotlin.threadsafe.createThreadSafeStore

// Data class to hold the state of the app
data class AppState(
    val user: PlayerProps = userSlice.initialState,
    val localPlay: LocalPlayConfig = LocalPlaySlice.initialState
)

// Root reducer function to handle actions and update the state
fun rootReducer(state: AppState, action: Any): AppState {
    return AppState(
        user = userSlice.reducer(state.user, action),
        localPlay = LocalPlaySlice.localPlayReducer(state.localPlay, action)
    )
}

// Create a thread safe store with the rootReducer and initial AppState
val store = createThreadSafeStore(::rootReducer, AppState())

// Subscribe to the store to log state updates
val unsubscribe = store.subscribe {
    Log.v("Store Updated", "State updated: ${store.state}")
}