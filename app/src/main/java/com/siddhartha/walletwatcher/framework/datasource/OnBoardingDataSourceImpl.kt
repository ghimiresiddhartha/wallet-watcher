package com.siddhartha.walletwatcher.framework.datasource

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.Gson
import com.siddhartha.walletwatcher.data.datasource.OnBoardingDataSource
import com.siddhartha.walletwatcher.data.model.onboarding.PhoneAuthDetails
import com.siddhartha.walletwatcher.data.model.onboarding.PhoneSmsResponse
import com.siddhartha.walletwatcher.common.exception.PhoneException
import com.siddhartha.walletwatcher.data.model.onboarding.UserData
import com.siddhartha.walletwatcher.data.model.onboarding.VerifiedPhoneDetails
import com.siddhartha.walletwatcher.framework.local.db.AppDb
import com.siddhartha.walletwatcher.framework.model.EncryptDataItem
import com.siddhartha.walletwatcher.util.AppConstant.IS_NEW_USER
import com.siddhartha.walletwatcher.util.AppConstant.UID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class OnBoardingDataSourceImpl @Inject constructor(
    @Named("encrypt") private val cipherEncrypt: Cipher,
    @Named("decrypt") private val cipherDecrypt: Cipher,
    @Named("shared") private val sharedPref: SharedPreferences,
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
                        verificationId = p0, resendToken = p1
                    )
                    resultLiveData.postValue(Result.success(phoneSmsResponse))
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    phoneSmsResponse.phoneAuthDetails = PhoneAuthDetails(
                        verificationId = p0, resendToken = null
                    )
                    resultLiveData.postValue(Result.success(phoneSmsResponse))
                }
            })
        }
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions.build())
        return resultLiveData
    }

    override fun verifyOtp(
        coroutineScope: CoroutineScope, phoneAuthCredential: PhoneAuthCredential
    ): LiveData<Result<UserData>> {
        val resultLiveData = MutableLiveData<Result<UserData>>()
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = UserData(
                    null,
                    it.result.user?.uid,
                    screenName = it.result.additionalUserInfo?.username.toString(),
                    phoneNumber = cipherEncrypt.doFinal(
                        it.result.user?.phoneNumber.toString().toByteArray()
                    )
                )
                coroutineScope.launch {
                    appDb.userItemDao().insert(user)
                }
                resultLiveData.postValue(Result.success(user))
            } else {
                resultLiveData.postValue(Result.failure(PhoneException(it.exception?.message.toString())))
            }

        }.addOnFailureListener {
            resultLiveData.postValue(Result.failure(PhoneException(it.message.toString())))
        }
        return resultLiveData
    }

    override suspend fun updateScreenNameInDatabase(uid: String?, screenName: String?) {
        if (uid != null && screenName != null) appDb.userItemDao().updateUsername(uid, screenName)
    }

    override fun getUserData(uid: String?): Flow<UserData?> =
        appDb.userItemDao().getUserData(uid.toString())

    override fun saveUidOfCurrentUser(uid: String) {
        val cipherEncryptedData = cipherEncrypt.doFinal(uid.toByteArray())
        sharedPref.edit().putString(UID, Gson().toJson(EncryptDataItem(cipherEncryptedData)))
            .apply()
    }

    override fun getUidOfCurrentUser(): String {
        val encryptedUid = sharedPref.getString(UID, null)
        return encryptedUid?.let {
            val encryptedDataItem = Gson().fromJson(encryptedUid, EncryptDataItem::class.java)
            val cipherDecryptedData = cipherDecrypt.doFinal(encryptedDataItem.cipherText)
            String(cipherDecryptedData)
        } ?: ""
    }

    override fun getNewUserStatus(): Boolean = sharedPref.getBoolean(IS_NEW_USER, true)

    override fun setNewUserStatus(status: Boolean) =
        sharedPref.edit().putBoolean(IS_NEW_USER, status).apply()

}