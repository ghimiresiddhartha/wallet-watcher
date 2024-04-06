package com.siddhartha.walletwatcher.util.ext

import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.siddhartha.walletwatcher.R

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.preventTwoClick() {
    this.isEnabled = false
    this.postDelayed({ this.isEnabled = true }, 500)
}

inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, length)
    val snackBarView: View = snack.view
    snackBarView.setBackgroundColor(ContextCompat.getColor(snack.context, R.color.color_accent))
    snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        .setTextColor(
            ContextCompat.getColor(snack.context, R.color.background)
        )
    snack.f()
    snack.show()
}