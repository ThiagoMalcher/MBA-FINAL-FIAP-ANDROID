package com.mba.tmalcher.fiapandroid.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Helpers(private val context: Context) {

    fun isValidEmail(email: String): Boolean {
        val emailPattern = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
        return emailPattern.matches(email)
    }

    fun isPasswordValid(password: String): Boolean {
        val pattern = "[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+".toRegex()
        return pattern.containsMatchIn(password)
    }

    fun requestPermissions(activity: Activity) {
        val notGrantedPermissions = mutableListOf<String>()
        val dangerousPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_DOCUMENTS,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.INTERNET
        )

        for (perm in dangerousPermissions) {
            if (ContextCompat.checkSelfPermission(context, perm)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notGrantedPermissions.add(perm)
            }
        }

        if (notGrantedPermissions.isNotEmpty()) {
            val requestedPerms = notGrantedPermissions.toTypedArray()
            ActivityCompat.requestPermissions(
                activity,
                requestedPerms,
                0
            )
        }
    }

}