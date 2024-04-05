package com.siddhartha.walletwatcher.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.util.Base64
import android.view.View
import androidx.core.content.ContextCompat
import com.siddhartha.walletwatcher.BuildConfig
import com.siddhartha.walletwatcher.WalletWatcherApp
import com.siddhartha.walletwatcher.R
import com.siddhartha.walletwatcher.common.PhoneException
import com.siddhartha.walletwatcher.domain.model.onboarding.PhoneSmsResponse
import java.security.InvalidAlgorithmParameterException
import java.util.Objects
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

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

    val encryptData: (message: String) -> ByteArray =
        { message: String ->
            try {
                val cipher = Cipher.getInstance(BuildConfig.ALGORITHM)
                cipher.init(
                    Cipher.ENCRYPT_MODE,
                    SecretKeySpec(BuildConfig.SECRET_KEY.toByteArray(), BuildConfig.MODE),
                    IvParameterSpec(BuildConfig.IV_KEY.toByteArray())
                )
                cipher.doFinal(message.toByteArray())
            } catch (e: Exception) {
                throw InvalidAlgorithmParameterException()
            }
        }

    val decryptData: (cypherText: ByteArray) -> String =
        { cypherText: ByteArray ->
            val cipher = Cipher.getInstance(BuildConfig.ALGORITHM)
            cipher.init(
                Cipher.DECRYPT_MODE,
                SecretKeySpec(BuildConfig.SECRET_KEY.toByteArray(), BuildConfig.MODE),
                IvParameterSpec(BuildConfig.IV_KEY.toByteArray())
            )
            String(cipher.doFinal(cypherText))
        }
}