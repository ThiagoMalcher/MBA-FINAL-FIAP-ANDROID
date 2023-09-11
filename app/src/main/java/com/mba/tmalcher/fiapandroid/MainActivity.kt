package com.mba.tmalcher.fiapandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mba.tmalcher.fiapandroid.activities.ProductList
import com.mba.tmalcher.fiapandroid.activities.RecoverPassword
import com.mba.tmalcher.fiapandroid.activities.RegisterUser
import com.mba.tmalcher.fiapandroid.firebase.Login
import com.mba.tmalcher.fiapandroid.utils.Helpers

class MainActivity : AppCompatActivity() {

    private lateinit var mInputUser: TextView
    private lateinit var mPassword: TextView
    private lateinit var mRegisterAccount: TextView
    private lateinit var mBtnLogin: Button
    private lateinit var mTextForgotPassw: TextView

    private val mFirebaseuser = Login()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //request permissions
        Helpers(applicationContext).requestPermissions(this)

        mInputUser = findViewById(R.id.inputUsername)
        mPassword = findViewById(R.id.inputPassword)
        mRegisterAccount = findViewById(R.id.textCreateNewAccount)
        mBtnLogin = findViewById(R.id.btnLogin)
        mTextForgotPassw = findViewById(R.id.textForgotPassw)

        if(mFirebaseuser.getCurrentUser() != null) {
            goToProductList()
        }

        mInputUser.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mPassword.requestFocus()
                return@OnEditorActionListener true
            }
            false
        })

        mPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(mPassword.windowToken, 0)
                true
            } else {
                false
            }
        }

        mTextForgotPassw.setOnClickListener {
            val intent = Intent(this, RecoverPassword::class.java)
            startActivity(intent)
        }

        mRegisterAccount.setOnClickListener {
            val intent = Intent(this, RegisterUser::class.java)
            startActivity(intent)
        }

        mBtnLogin.setOnClickListener {
            val email = mInputUser.text.toString()
            val password = mPassword.text.toString()
            if(email.isNotEmpty() || password.isNotEmpty()) {
                if(Helpers(applicationContext).isValidEmail(email)) {
                    mFirebaseuser.loginUser(email, password) { success ->
                        if (success) {
                            goToProductList()
                        } else {
                            Toast.makeText(applicationContext, getString(R.string.msg_login_failure),
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else {
                    Toast.makeText(applicationContext, getString(R.string.msg_fields_email),
                        Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(applicationContext, getString(R.string.msg_fields),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToProductList() {
          val intent = Intent(this, ProductList::class.java)
          startActivity(intent)
          finish()
    }

}