package com.mba.tmalcher.fiapandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mba.tmalcher.fiapandroid.activities.ProductList
import com.mba.tmalcher.fiapandroid.activities.RegisterUser
import com.mba.tmalcher.fiapandroid.firebase.Login
import com.mba.tmalcher.fiapandroid.utils.Helpers

class MainActivity : AppCompatActivity() {

    private lateinit var mInputUser: TextView
    private lateinit var mPassword: TextView
    private lateinit var mRegisterAccount: TextView
    private lateinit var mBtnLogin: Button

    private val mFirebaseuser = Login()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //request permissions
        Helpers(applicationContext).requestPermissions(this)

        mInputUser = findViewById<TextView>(R.id.inputUsername)
        mPassword = findViewById<TextView>(R.id.inputPassword)
        mRegisterAccount = findViewById(R.id.textCreateNewAccount)
        mBtnLogin = findViewById(R.id.btnLogin)


        if(mFirebaseuser.getCurrentUser() != null) {
            goToProductList()
        }

        mRegisterAccount.setOnClickListener {
            val intent = Intent(this, RegisterUser::class.java)
            startActivity(intent)
            finish()
        }

        mBtnLogin.setOnClickListener {
            val email = mInputUser.text.toString()
            val password = mPassword.text.toString()
            if(!email.isEmpty() || !password.isEmpty()) {
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

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    fun goToProductList() {
          val intent = Intent(this, ProductList::class.java)
          startActivity(intent)
          finish()
    }

}