package com.mba.tmalcher.fiapandroid.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import com.mba.tmalcher.fiapandroid.MainActivity
import com.mba.tmalcher.fiapandroid.R
import com.mba.tmalcher.fiapandroid.firebase.Authentication

class LogoutDialog(private val context: Context) {

    fun show() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.logout))
        builder.setMessage(context.getString(R.string.logout_message))

        builder.setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        builder.setPositiveButton(context.getString(R.string.confirm)) { _, _ ->
            Authentication().logout()
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        val dialog = builder.create()
        dialog.show()
    }
}