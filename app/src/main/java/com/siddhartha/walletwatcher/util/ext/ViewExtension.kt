package com.siddhartha.walletwatcher.util.ext

import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
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

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.invisible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

fun View.preventTwoClick() {
    this.isEnabled = false
    this.postDelayed({ this.isEnabled = true }, 500)
}

inline fun View.snack(
    @StringRes messageRes: Int,
    length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit
) {
    snack(resources.getString(messageRes), length, f)
}

inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, length)
    val snackBarView: View = snack.view
    snackBarView.setBackgroundColor(ContextCompat.getColor(snack.context, R.color.color_accent))
    snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).setTextColor(
        ContextCompat.getColor(snack.context, R.color.background)
    )
    snack.f()
    snack.show()
}