package com.cyphermoon.tictaczone.data


import androidx.compose.runtime.rememberCoroutineScope
import com.cyphermoon.tictaczone.redux.FirestoreOnlinePlayerProps
import com.google.firebase.firestore.FirebaseFirestore
import com.cyphermoon.tictaczone.redux.PlayerProps
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = FirebaseFirestore.getInstance()


    suspend fun getOrCreateUser(userId: String, user: FirestoreOnlinePlayerProps) {
        val docRef = db.collection("users").document(userId)

        val userSnapShot = docRef.get().await()

        if (!userSnapShot.exists()) {
            docRef.set(user).await()
        }
    }
    fun listenForUserUpdates(userId: String, onUpdate: (PlayerProps?) -> Unit) {
        val docRef = db.collection("users").document(userId)

        docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Log the error or handle it as needed
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val user = snapshot.toObject(PlayerProps::class.java)
                onUpdate(user)
            } else {
                onUpdate(null)
            }
        }
    }
}