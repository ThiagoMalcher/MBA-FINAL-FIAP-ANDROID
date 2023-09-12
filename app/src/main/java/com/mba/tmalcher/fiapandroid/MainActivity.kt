package com.mba.tmalcher.fiapandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast.*
import androidx.appcompat.app.AppCompatActivity
import com.mba.tmalcher.fiapandroid.activities.ProductList
import com.mba.tmalcher.fiapandroid.activities.RecoverPassword
import com.mba.tmalcher.fiapandroid.activities.RegisterUser
import com.mba.tmalcher.fiapandroid.firebase.Authentication
import com.mba.tmalcher.fiapandroid.utils.Helpers
import com.mba.tmalcher.fiapandroid.utils.InputHelper
import com.mba.tmalcher.fiapandroid.utils.Validators

class MainActivity : AppCompatActivity() {

    private lateinit var mInputUser: TextView
    private lateinit var mPassword: TextView
    private lateinit var mRegisterAccount: TextView
    private lateinit var mBtnLogin: Button
    private lateinit var mTextForgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //request permissions
        Helpers(applicationContext).requestPermissions(this)

        mInputUser = findViewById(R.id.inputUsername)
        mPassword = findViewById(R.id.inputPassword)
        mRegisterAccount = findViewById(R.id.textCreateNewAccount)
        mBtnLogin = findViewById(R.id.btnLogin)
        mTextForgotPassword = findViewById(R.id.textForgotPassw)

        if(Authentication().isLogged()) {
            navigateToProductList()
        }

        InputHelper(this).setNextOnDone(actual = mInputUser, next = mPassword)
        InputHelper(this).closeKeyboardOnDone(mPassword)

        mTextForgotPassword.setOnClickListener {
            val intent = Intent(this, RecoverPassword::class.java)
            startActivity(intent)
        }

        mRegisterAccount.setOnClickListener {
            val intent = Intent(this, RegisterUser::class.java)
            startActivity(intent)
        }

        mBtnLogin.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val email = mInputUser.text.toString()
        val password = mPassword.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            makeText(applicationContext, getString(R.string.msg_fields), LENGTH_SHORT).show()
            return
        }

        if (!Validators().isEmailValid(email)) {
            makeText(applicationContext, getString(R.string.msg_fields_email), LENGTH_SHORT).show()
            return
        }

        Authentication().signInWith(email, password) { wasSuccess ->
            when(wasSuccess) {
                true -> navigateToProductList()
                false -> makeText(applicationContext, getString(R.string.msg_login_failure), LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToProductList() {
          val intent = Intent(this, ProductList::class.java)
          startActivity(intent)
          finish()
    }
}