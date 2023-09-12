package com.mba.tmalcher.fiapandroid.utils

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

class InputHelper(private val context: Context) {
    fun setNextOnDone(actual: TextView, next: TextView) {
        actual.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == IME_ACTION_DONE) {
                next.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    fun closeKeyboardOnDone(editText: TextView) {
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == IME_ACTION_DONE) {
                val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editText.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }
    }
}