package com.mba.tmalcher.fiapandroid.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mba.tmalcher.fiapandroid.MainActivity
import com.mba.tmalcher.fiapandroid.R
import com.mba.tmalcher.fiapandroid.firebase.Login
import com.mba.tmalcher.fiapandroid.utils.Helpers

class RecoverPassword : AppCompatActivity() {

    private lateinit var mBtnSendEmail: Button
    private lateinit var mInputEmail: TextView
    val auth = FirebaseAuth.getInstance()
    private val mFirebaseuser = Login()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)


        mBtnSendEmail =  findViewById<Button>(R.id.btnSendEmail)
        mInputEmail = findViewById<TextView>(R.id.inputUserEmail)

        mBtnSendEmail.setOnClickListener {
            val email = mInputEmail.text.toString()
            if (email.isNotEmpty()) {
                if (Helpers(applicationContext).isValidEmail(email)) {
                    mFirebaseuser.recoverLogin(email) { isSuccess ->
                        if (isSuccess) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {

                        }
                    }
                }
            }
        }
    }
}