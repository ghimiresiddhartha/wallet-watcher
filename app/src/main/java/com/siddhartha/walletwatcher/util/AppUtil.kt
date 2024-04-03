package com.siddhartha.walletwatcher.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import com.siddhartha.walletwatcher.WalletWatcherApp
import com.siddhartha.walletwatcher.R
import java.util.Objects

object AppUtil {

    fun changeDeviceBarColor(activity: Activity){
        setNavigationBarButtonsColor(activity, ContextCompat.getColor(activity, R.color.background))
    }

    fun isConnection(): Boolean {
        val connectivityManager = WalletWatcherApp.INSTANCE
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo =
            Objects.requireNonNull(connectivityManager)
                .activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun preventTwoClick(view: View?) {
        if (view != null) {
            view.isEnabled = false
            view.postDelayed(Runnable { view.isEnabled = true }, 800)
        }
    }

    private fun setNavigationBarButtonsColor(activity: Activity, navigationBarColor: Int) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O){
            val decorView = activity.window.decorView
            var flags = decorView.systemUiVisibility
            flags = if (isColorLight(navigationBarColor)) {
                flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
            decorView.systemUiVisibility = flags
        }
    }

    private fun isColorLight(color: Int): Boolean {
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness < 0.5
    }
}