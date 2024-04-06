package com.siddhartha.walletwatcher.domain.repository

import androidx.lifecycle.LiveData
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.siddhartha.walletwatcher.domain.model.onboarding.PhoneSmsResponse
import com.siddhartha.walletwatcher.domain.model.onboarding.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface OnBoardingRepository {
    fun sendOtp(
        phoneNumber: String, phoneAuthOptions: PhoneAuthOptions.Builder
    ): LiveData<Result<PhoneSmsResponse>>

    fun verifyOtp(
        coroutineScope: CoroutineScope, phoneAuthCredential: PhoneAuthCredential
    ): LiveData<Result<UserData>>

    suspend fun updateScreenNameInDatabase(uid: String?, screenName: String?)

    fun saveUidOfCurrentUser(uid: String)

    fun getUserData(uid: String?): Flow<UserData?>

    fun getUidOfCurrentUser(): String

    fun getNewUserStatus(): Boolean

    fun setNewUserStatus(status: Boolean)

}