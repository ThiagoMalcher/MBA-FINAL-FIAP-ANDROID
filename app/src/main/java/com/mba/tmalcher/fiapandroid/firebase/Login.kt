package com.mba.tmalcher.fiapandroid.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Login {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    fun loginUser(email: String, password: String, callback: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }
}
