package com.siddhartha.walletwatcher.data.datasource

import androidx.lifecycle.LiveData
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.siddhartha.walletwatcher.data.model.onboarding.UserData
import com.siddhartha.walletwatcher.data.model.onboarding.PhoneSmsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface OnBoardingDataSource {
    fun sendOtp(
        phoneNumber: String,
        phoneAuthOptions: PhoneAuthOptions.Builder,
    ): LiveData<Result<PhoneSmsResponse>>

    fun verifyOtp(
        coroutineScope: CoroutineScope, phoneAuthCredential: PhoneAuthCredential
    ): LiveData<Result<UserData>>

    suspend fun updateScreenNameInDatabase(uid: String?, screenName: String?)

    fun getUserData(uid: String?): Flow<UserData?>

    fun saveUidOfCurrentUser(uid: String)

    fun getUidOfCurrentUser(): String

    fun getNewUserStatus(): Boolean

    fun setNewUserStatus(status: Boolean)
}