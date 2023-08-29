package com.mba.tmalcher.fiapandroid.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mba.tmalcher.fiapandroid.MainActivity
import com.mba.tmalcher.fiapandroid.R
import com.mba.tmalcher.fiapandroid.firebase.Register
import com.mba.tmalcher.fiapandroid.utils.Helpers

class RegisterUser : AppCompatActivity() {

    private lateinit var mInputEmail: TextView
    private lateinit var mPassword: TextView
    private lateinit var mUserName: TextView
    private lateinit var mBtnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        mInputEmail = findViewById<TextView>(R.id.inputtxtUserEmail)
        mPassword = findViewById<TextView>(R.id.inputPassword)
        mUserName = findViewById<TextView>(R.id.inputtxtUsername)
        mBtnRegister = findViewById<Button>(R.id.btnRegister)

        mBtnRegister.setOnClickListener(View.OnClickListener {
            registerUser();
        })
    }

    private fun registerUser() {
        //local variable
        val txtEmail = mInputEmail.text.toString()
        val txtPassword = mPassword.text.toString()
        val txtUserName = mUserName.text.toString()

        if(!txtUserName.isEmpty() || !txtPassword.isEmpty() || !txtEmail.isEmpty()) {
            if(Helpers(applicationContext).isValidEmail(txtEmail) ||
                Helpers(applicationContext).isPasswordValid(txtPassword)) {

                val firebase = Register(applicationContext)
                firebase.registerUser( txtEmail, txtPassword, txtUserName) { success ->
                    if (success) {
                        Toast.makeText(applicationContext, getString(R.string.msg_firebase_register_successfully),
                            Toast.LENGTH_SHORT).show()
                        goToMain()
                    } else {
                        Toast.makeText(applicationContext, getString(R.string.msg_firebase_register_failure),
                            Toast.LENGTH_SHORT).show()
                        cleanInputText()
                    }
                }
            }
            else {
                Toast.makeText(applicationContext, getString(R.string.msg_fields_email),
                    Toast.LENGTH_SHORT).show()
                cleanInputText()
            }
        }
        else {
            Toast.makeText(applicationContext, getString(R.string.msg_fields),
                Toast.LENGTH_SHORT).show()
            cleanInputText()
        }
    }

    fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun cleanInputText() {
        mInputEmail.setText("")
        mPassword.setText("")
        mUserName.setText("")
    }
}