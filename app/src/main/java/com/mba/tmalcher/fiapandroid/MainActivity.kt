package com.mba.tmalcher.fiapandroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mba.tmalcher.fiapandroid.activities.RegisterUser

class MainActivity : AppCompatActivity() {

    private lateinit var mInputUser: TextView
    private lateinit var mPassword: TextView
    private lateinit var mRegisterAccount: TextView
    private lateinit var mBtnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mInputUser = findViewById<TextView>(R.id.inputUsername)
        mPassword = findViewById<TextView>(R.id.inputPassword)
        mRegisterAccount = findViewById(R.id.textCreateNewAccount)

        mRegisterAccount.setOnClickListener {
            val intent = Intent(this, RegisterUser::class.java)
            startActivity(intent)
            finish()
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

}