package com.cyphermoon.tictaczone.data


import com.google.firebase.firestore.FirebaseFirestore
import com.cyphermoon.tictaczone.redux.PlayerProps
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = FirebaseFirestore.getInstance()


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