package com.mba.tmalcher.fiapandroid.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.mba.tmalcher.fiapandroid.MainActivity
import com.mba.tmalcher.fiapandroid.R
import com.mba.tmalcher.fiapandroid.firebase.Authentication
import com.mba.tmalcher.fiapandroid.utils.InputHelper
import com.mba.tmalcher.fiapandroid.utils.Validators

class RecoverPassword : AppCompatActivity() {

    private lateinit var mBtnSendEmail: Button
    private lateinit var mInputEmail: TextView
    private val mFirebaseuser = Authentication()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)

        mBtnSendEmail =  findViewById(R.id.btnSendEmail)
        mInputEmail = findViewById(R.id.inputUserEmail)

        InputHelper(this).closeKeyboardOnDone(mInputEmail)

        mBtnSendEmail.setOnClickListener {
            recover()
        }
    }

    private fun recover() {
        val email = mInputEmail.text.toString()

        if(email.isEmpty()) {
            Toast.makeText(applicationContext, getString(R.string.msg_fields), LENGTH_SHORT).show()
            return
        }

        if (!Validators().isEmailValid(email)) {
            Toast.makeText(applicationContext, getString(R.string.msg_fields_email_invalid), LENGTH_SHORT).show()
            return
        }

        mFirebaseuser.changePassword(email) {
            Toast.makeText(applicationContext, getString(R.string.msg_email_sent), LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}