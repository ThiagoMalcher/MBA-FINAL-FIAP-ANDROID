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

class RegisterUser : AppCompatActivity() {

    private lateinit var mInputEmail: TextView
    private lateinit var mPassword: TextView
    private lateinit var mUserName: TextView
    private lateinit var mBtnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        mInputEmail = findViewById(R.id.inputtxtUserEmail)
        mPassword = findViewById(R.id.inputPassword)
        mUserName = findViewById(R.id.inputtxtUsername)
        mBtnRegister = findViewById(R.id.btnRegister)

        InputHelper(this).setNextOnDone(actual = mInputEmail, next = mUserName)
        InputHelper(this).setNextOnDone(actual = mUserName, next = mPassword)
        InputHelper(this).closeKeyboardOnDone(mPassword)

        mBtnRegister.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val txtEmail = mInputEmail.text.toString()
        val txtPassword = mPassword.text.toString()
        val txtUserName = mUserName.text.toString()

        if (txtUserName.isEmpty() || txtPassword.isEmpty() || txtEmail.isEmpty()) {
            Toast.makeText(applicationContext, getString(R.string.msg_fields), LENGTH_SHORT).show()
            return
        }

        if (!Validators().isEmailValid(txtEmail) || !Validators().isPasswordValid(txtPassword)) {
            Toast.makeText(applicationContext, getString(R.string.msg_fields_email), LENGTH_SHORT)
                .show()
            return
        }

        Authentication().signUpWith(txtEmail, txtPassword, txtUserName) { wasSuccess ->
            when (wasSuccess) {
                true -> {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.msg_firebase_register_successfully),
                        LENGTH_SHORT
                    ).show()
                    navigateToMain()
                }
                false -> {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.msg_firebase_register_failure),
                        LENGTH_SHORT
                    ).show()
                    clearForm()
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun clearForm() {
        mInputEmail.text = ""
        mPassword.text = ""
        mUserName.text = ""
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}