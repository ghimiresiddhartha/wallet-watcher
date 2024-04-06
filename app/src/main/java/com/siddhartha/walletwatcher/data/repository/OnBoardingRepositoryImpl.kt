package com.siddhartha.walletwatcher.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.siddhartha.walletwatcher.data.datasource.OnBoardingDataSource
import com.siddhartha.walletwatcher.data.mapper.toPhoneAuthDetails
import com.siddhartha.walletwatcher.data.mapper.toPhoneSmsDetails
import com.siddhartha.walletwatcher.data.mapper.toUserData
import com.siddhartha.walletwatcher.domain.model.onboarding.PhoneSmsResponse
import com.siddhartha.walletwatcher.domain.model.onboarding.UserData
import com.siddhartha.walletwatcher.domain.repository.OnBoardingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnBoardingRepositoryImpl @Inject constructor(private val onBoardingDataSource: OnBoardingDataSource) :
    OnBoardingRepository {
    override fun sendOtp(
        phoneNumber: String, phoneAuthOptions: PhoneAuthOptions.Builder
    ): LiveData<Result<PhoneSmsResponse>> =
        onBoardingDataSource.sendOtp(phoneNumber, phoneAuthOptions).map { result ->
            result.map {
                PhoneSmsResponse(
                    it.phoneSmsDetails?.toPhoneSmsDetails(),
                    it.phoneAuthDetails?.toPhoneAuthDetails(),
                    it.message
                )
            }
        }

    override fun verifyOtp(
        coroutineScope: CoroutineScope, phoneAuthCredential: PhoneAuthCredential
    ): LiveData<Result<UserData>> =
        onBoardingDataSource.verifyOtp(coroutineScope, phoneAuthCredential).map { result ->
            result.map {
                it.toUserData()
            }
        }

    override suspend fun updateScreenNameInDatabase(uid: String?, screenName: String?) =
        onBoardingDataSource.updateScreenNameInDatabase(uid, screenName)

    override fun saveUidOfCurrentUser(uid: String) = onBoardingDataSource.saveUidOfCurrentUser(uid)

    override fun getUserData(uid: String?): Flow<UserData?> =
        onBoardingDataSource.getUserData(uid).map {
            it?.toUserData()
        }

    override fun getUidOfCurrentUser(): String = onBoardingDataSource.getUidOfCurrentUser()

    override fun getNewUserStatus(): Boolean = onBoardingDataSource.getNewUserStatus()

    override fun setNewUserStatus(status: Boolean) = onBoardingDataSource.setNewUserStatus(status)
}