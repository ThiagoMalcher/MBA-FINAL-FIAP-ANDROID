package com.mba.tmalcher.fiapandroid.utils

import android.content.Context

class helpers(private val context: Context) {

    fun isValidEmail(email: String): Boolean {
        val emailPattern = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
        return emailPattern.matches(email)
    }

    fun isPasswordValid(password: String): Boolean {
        val pattern = "[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+".toRegex()
        return pattern.containsMatchIn(password)
    }
}