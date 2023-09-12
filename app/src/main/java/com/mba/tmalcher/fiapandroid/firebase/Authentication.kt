package com.mba.tmalcher.fiapandroid.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class Authentication {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun isLogged(): Boolean {
        return auth.currentUser != null
    }

    fun signUpWith(
        email: String,
        password: String,
        nickname: String,
        onComplete: (Boolean) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(nickname)
                        .build()

                    auth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileTask ->
                            onComplete(profileTask.isSuccessful)
                        }
                } else {
                    onComplete(false)
                }
            }
    }

    fun signInWith(email: String, password: String, callback: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    fun changePassword(email: String, callback: () -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                callback()
            }
    }

    fun logout() {
        auth.signOut()
    }
}
