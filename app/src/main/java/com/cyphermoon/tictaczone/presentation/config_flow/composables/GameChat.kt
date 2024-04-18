package com.cyphermoon.tictaczone.presentation.config_flow.composables

import android.content.ContentValues.TAG
import android.nfc.Tag
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cyphermoon.tictaczone.presentation.game_flow.utils.firestoreDB
import com.cyphermoon.tictaczone.ui.theme.LightSecondary
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow

data class Message(
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val sender: String = "",
    val name: String = ""
)

val messagesFlow = MutableStateFlow<List<Message>>(listOf())

fun listenForMessageUpdates(gameId: String) {
    val messagesRef = firestoreDB.collection("chats").document(gameId).collection("messages")
    messagesRef.orderBy("timestamp", Query.Direction.ASCENDING)
        .addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(TAG, "Listen failed.", error)
                return@addSnapshotListener
            }

            val messages = mutableListOf<Message>()
            for (doc in value!!) {
                Log.v(TAG, doc.toObject(Message::class.java).text)
                messages.add(doc.toObject(Message::class.java))
            }
            messagesFlow.value = messages
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameChat(gameId: String, currentPlayerId: String, currentPlayerName: String) {
    val messages by messagesFlow.collectAsState()
    val message = remember { mutableStateOf("") }
    val currentMessage = message.value
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = gameId) {
        // set up a listener for real-time message
        listenForMessageUpdates(gameId)
    }

    fun sendMessage(
        gameId: String,
        currentPlayerId: String,
        currentPlayerName: String,
        messageText: String
    ) {
        val messagesRef = firestoreDB.collection("chats").document(gameId).collection("messages")

        // Validate the message
        if (messageText.trim().isEmpty()) {
            Log.w(TAG, "Message cannot be empty")
            return
        }

        // Add the message to Firestore
        val firestoreMessage = hashMapOf(
            "text" to messageText,
            "timestamp" to Timestamp.now(),
            "sender" to currentPlayerId,
            "name" to currentPlayerName
        )
        messagesRef.add(firestoreMessage)

        // Clear the input field
        message.value = ""
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(LightSecondary)
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(text = "Chat Room", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(8.dp))

        messages.forEachIndexed { _, message ->
            ChatMessage(
                text = message.text,
                id = message.sender,
                name = message.name,
                right = message.sender == currentPlayerId
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row{
            // ChatInput
            TextField(
                value = currentMessage,
                onValueChange = { message.value = it},
                placeholder = { Text("Send Message.....") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    IconButton(onClick = { sendMessage(gameId, currentPlayerId, currentPlayerName, currentMessage) }) {
                        Icon(Icons.Default.Send, contentDescription = "Send Message")
                    }
                },

                keyboardActions = KeyboardActions(onDone = {
                    sendMessage(gameId, currentPlayerId, currentPlayerName, currentMessage)
                    keyboardController?.hide()
                }),
                singleLine = true

            )

        }

    }
}
    @Preview(showBackground = true)
    @Composable
    fun PreviewGameChat() {
        GameChat(gameId = "sampleGameId", currentPlayerId = "samplePlayerId", currentPlayerName="Kelvin")
    }