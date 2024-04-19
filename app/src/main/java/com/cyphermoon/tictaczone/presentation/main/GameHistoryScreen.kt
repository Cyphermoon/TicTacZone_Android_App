package com.cyphermoon.tictaczone.presentation.main

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyphermoon.tictaczone.presentation.game_flow.utils.firestoreDB
import com.cyphermoon.tictaczone.presentation.main.components.Logo
import com.cyphermoon.tictaczone.redux.store
import com.cyphermoon.tictaczone.ui.theme.LightSecondary
import kotlinx.coroutines.flow.MutableStateFlow

val gameHistoryFlow = MutableStateFlow<List<GameHistoryProps>>(listOf())

fun setupGameHistoryListener(userId: String) {
    val gameHistoryRef = firestoreDB.collection("users").document(userId).collection("history")

    gameHistoryRef.addSnapshotListener { snapshot, error ->
        if (error != null) {
            Log.w(TAG, "Listen failed.", error)
            return@addSnapshotListener
        }

        if (snapshot != null && !snapshot.isEmpty) {
            val gameHistory = snapshot.toObjects(GameHistoryProps::class.java)
            gameHistoryFlow.value = gameHistory
        } else {
            Log.d(TAG, "Current data: null")
        }
    }
}

@Composable
fun GameHistoryScreen(){
    val gameHistory by gameHistoryFlow.collectAsState()
    val currentPlayerId = remember { mutableStateOf(store.getState().user.id) }

    LaunchedEffect(key1 = currentPlayerId.value){
        setupGameHistoryListener(currentPlayerId.value)
    }

    DisposableEffect(key1 = store.getState().user.id) {
        val unsubscribe = store.subscribe{
            currentPlayerId.value = store.getState().user.id
        }

        onDispose {
            unsubscribe()
        }
    }

    Column {
        Logo(name = null)

        Column(
            modifier = Modifier.padding(10.dp)
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Game History",
                fontSize = 17.sp,
            )

            Spacer(modifier = Modifier.height(10.dp))

            when {
                gameHistory == null -> Text(text = "Loading...")
                gameHistory.isEmpty() -> Text(text = "No games played yet")
                else -> {
                    Column() {
                        HeaderRow()
                        LazyColumn {
                            items(gameHistory.size) { index ->
                                GameHistoryRow(gameHistory[index])
                            }
                        }
                    }
                }
            }
        }
    }


 }

@Composable
fun GameHistoryRow(game: GameHistoryProps) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = game.opponent, modifier = Modifier.weight(1f), fontSize=11.sp)
        Text(text = game.gameType, modifier = Modifier
            .padding(18.dp, 0.dp, 0.dp, 0.dp)
            .weight(1f), fontSize=11.sp)
        Text(text = game.firstToWin.toString(), modifier = Modifier.weight(1f), fontSize=11.sp)
        Text(text = game.result, modifier = Modifier.weight(1f), fontSize=11.sp)
    }
}

@Composable
fun HeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightSecondary)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Opponent", modifier = Modifier.weight(1f))
        Text(text = "Game Type", modifier = Modifier
            .padding(18.dp, 0.dp, 0.dp, 0.dp)
            .weight(1f))
        Text(text = "First To Win", modifier = Modifier.weight(1f))
        Text(text = "Result", modifier = Modifier.weight(1f))
    }
}

data class GameHistoryProps(
    val opponent: String = "",
    val gameType: String = "",
    val firstToWin: Int = 0,
    val result: String = "N/A"
)