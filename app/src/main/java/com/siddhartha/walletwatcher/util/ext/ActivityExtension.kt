package com.siddhartha.walletwatcher.util.ext

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.siddhartha.walletwatcher.presentation.home.HomeActivity

fun Activity.openHomeActivity(
    extras: Bundle.() -> Unit = {}
) {
    val intent = Intent(this, HomeActivity::class.java)
    intent.putExtras(Bundle().apply(extras))
    try {
        startActivity(intent)
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}