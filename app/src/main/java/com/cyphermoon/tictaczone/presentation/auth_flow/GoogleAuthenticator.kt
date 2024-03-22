package com.cyphermoon.tictaczone.presentation.auth_flow

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.cyphermoon.tictaczone.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class GoogleAuthenticator(
    private val context: Context,
) {
    private val auth: FirebaseAuth = Firebase.auth
    private val oneTapClient = Identity.getSignInClient(context)
    private val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(true)
                .build()
        )
        .build()

    suspend fun signInWithGoogle(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(signInRequest).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): SignInResultData {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val firebaseCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

        // return the user data
        return try {
            // get the user data
            val user = auth.signInWithCredential(firebaseCredentials).await().user
            // update the user data class
            SignInResultData(
                data = user?.run {
                    FirebaseUserData(
                        uid = uid,
                        email = email,
                        displayName = displayName,
                        photoUrl = photoUrl.toString()
                    )
                },
                error = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResultData(data = null, error = e.message)
        }
    }

   // This function is used to get the currently signed in user's data
fun getSignedInUser(): FirebaseUserData? =
    // Check if there is a current user signed in
    auth.currentUser?.run {
        // If there is a user, create a new FirebaseUserData object with the user's data
        FirebaseUserData(
            uid = uid, // User's unique ID
            email = email, // User's email
            displayName = displayName, // User's display name
            photoUrl = photoUrl.toString() // User's photo URL
        )
    }
    // If there is no user signed in, this function will return null

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

}