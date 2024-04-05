package com.siddhartha.walletwatcher.presentation.onboarding

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthOptions.Builder
import com.google.firebase.auth.PhoneAuthProvider
import com.siddhartha.walletwatcher.R
import com.siddhartha.walletwatcher.common.PhoneException
import com.siddhartha.walletwatcher.domain.model.onboarding.PhoneSmsResponse
import com.siddhartha.walletwatcher.domain.model.onboarding.UserData
import com.siddhartha.walletwatcher.domain.repository.OnBoardingRepository
import com.siddhartha.walletwatcher.util.AppUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val application: Application,
    private val repository: OnBoardingRepository
) : ViewModel() {
    fun sendOtp(
        phoneNumber: String,
        phoneAuthOptions: Builder
    ): LiveData<Result<PhoneSmsResponse>> {
        val result = MutableLiveData<Result<PhoneSmsResponse>>()
        return if (AppUtil.isConnection()) {
            repository.sendOtp(phoneNumber, phoneAuthOptions)
        } else {
            result.value =
                Result.failure(PhoneException(application.getString(R.string.no_internet_connection)))
            result
        }
    }

    fun verifyOtp(phoneSmsResponse: PhoneSmsResponse): LiveData<Result<UserData>> {
        val result = MutableLiveData<Result<UserData>>()
        val credential = PhoneAuthProvider.getCredential(
            phoneSmsResponse.phoneAuthDetails!!.verificationId!!,
            phoneSmsResponse.phoneSmsDetails!!.smsCode!!
        )
        return if (AppUtil.isConnection()) {
            repository.verifyOtp(viewModelScope, credential)
        } else {
            result.value =
                Result.failure(PhoneException(application.getString(R.string.no_internet_connection)))
            result
        }
    }

    fun updateScreenName(uid: String, screenName: String) =
        viewModelScope.launch {
            repository.updateScreenNameInDatabase(uid, screenName)
        }

}