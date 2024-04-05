package com.siddhartha.walletwatcher.framework.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.siddhartha.walletwatcher.data.datasource.OnBoardingDataSource
import com.siddhartha.walletwatcher.data.model.onboarding.PhoneAuthDetails
import com.siddhartha.walletwatcher.data.model.onboarding.PhoneSmsResponse
import com.siddhartha.walletwatcher.common.PhoneException
import com.siddhartha.walletwatcher.data.model.onboarding.UserData
import com.siddhartha.walletwatcher.data.model.onboarding.VerifiedPhoneDetails
import com.siddhartha.walletwatcher.framework.local.db.AppDb
import com.siddhartha.walletwatcher.util.AppUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class OnBoardingDataSourceImpl @Inject constructor(
    @Named("encrypt") private val cipher: Cipher,
    private val appDb: AppDb
) : OnBoardingDataSource {
    override fun sendOtp(
        phoneNumber: String,
        phoneAuthOptions: PhoneAuthOptions.Builder,
    ): LiveData<Result<PhoneSmsResponse>> {
        val resultLiveData = MutableLiveData<Result<PhoneSmsResponse>>()
        val phoneSmsResponse = PhoneSmsResponse(null, null, null)
        phoneAuthOptions.apply {
            setTimeout(60L, TimeUnit.SECONDS)
            setPhoneNumber("+977$phoneNumber")
            setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    phoneSmsResponse.phoneSmsDetails = VerifiedPhoneDetails(p0.smsCode)
                    resultLiveData.postValue(Result.success(phoneSmsResponse))
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    phoneSmsResponse.message = p0.message.toString()
                    resultLiveData.postValue(Result.failure(PhoneException(phoneSmsResponse.message.toString())))
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    phoneSmsResponse.phoneAuthDetails = PhoneAuthDetails(
                        verificationId = p0,
                        resendToken = p1
                    )
                    resultLiveData.postValue(Result.success(phoneSmsResponse))
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    phoneSmsResponse.message = "Timeout!!!"
                    resultLiveData.postValue(Result.failure(PhoneException(phoneSmsResponse.message.toString())))
                }
            })
        }
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions.build())
        return resultLiveData
    }

    override fun verifyOtp(
        coroutineScope: CoroutineScope,
        phoneAuthCredential: PhoneAuthCredential
    ): LiveData<Result<UserData>> {
        val resultLiveData = MutableLiveData<Result<UserData>>()
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener {
                val user = UserData(
                    null,
                    it.result.user?.uid,
                    screenName = it.result.additionalUserInfo?.username.toString(),
                    phoneNumber = cipher.doFinal(
                        it.result.user?.phoneNumber.toString().toByteArray()
                    )
                )
                coroutineScope.launch {
                    appDb.userItemDao().insert(user)
                }
                resultLiveData.postValue(Result.success(user))
            }
            .addOnFailureListener {
                resultLiveData.postValue(Result.failure(PhoneException(it.message.toString())))
            }
        return resultLiveData
    }

    override suspend fun updateScreenNameInDatabase(uid: String?, screenName: String?) {
        if (uid != null && screenName != null)
            appDb.userItemDao().updateUsername(uid, screenName)
    }

}