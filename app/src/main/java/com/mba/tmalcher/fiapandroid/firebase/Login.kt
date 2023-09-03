package com.mba.tmalcher.fiapandroid.firebase

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
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

    fun recoverLogin(email: String, callback: (Boolean) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(OnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    // O email de recuperação foi enviado com sucesso
                    callback(true)
                } else {
                    // Ocorreu um erro ao enviar o email de recuperação
                    callback(false)
                }
            })
    }

}
