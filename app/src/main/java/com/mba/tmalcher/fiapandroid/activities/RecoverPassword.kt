package com.mba.tmalcher.fiapandroid.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
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

        mBtnSendEmail =  findViewById(R.id.btnSendEmail)
        mInputEmail = findViewById(R.id.inputUserEmail)

        mInputEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(mInputEmail.windowToken, 0)
                true
            } else {
                false
            }
        }

        mBtnSendEmail.setOnClickListener {
            val email = mInputEmail.text.toString()
            if (email.isNotEmpty()) {
                if (Helpers(applicationContext).isValidEmail(email)) {
                    mFirebaseuser.recoverLogin(email) { _ ->
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}