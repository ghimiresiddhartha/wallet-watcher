package com.siddhartha.walletwatcher

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WalletWatcherApp : Application(){

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        when(event){
            Lifecycle.Event.ON_START->{
                isApplicationRunning = true
            }
            Lifecycle.Event.ON_STOP->{
                isApplicationRunning = false
            }
            else->{}
        }
    }

    companion object {
        var INSTANCE: Application = WalletWatcherApp()
        var isApplicationRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleEventObserver)
        INSTANCE = this@WalletWatcherApp
    }
}