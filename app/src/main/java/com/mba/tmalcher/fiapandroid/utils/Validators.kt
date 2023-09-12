package com.mba.tmalcher.fiapandroid.utils

private const val PASSWORD_REGEX = "[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+"

private const val EMAIL_REGEX = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"

class Validators {
    fun isEmailValid(email: String): Boolean {
        return EMAIL_REGEX.toRegex().matches(email)
    }

    fun isPasswordValid(password: String): Boolean {
        return PASSWORD_REGEX.toRegex().containsMatchIn(password)
    }
}