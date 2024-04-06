package com.siddhartha.walletwatcher.presentation.onboarding

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthOptions.Builder
import com.google.firebase.auth.PhoneAuthProvider
import com.siddhartha.walletwatcher.R
import com.siddhartha.walletwatcher.common.exception.PhoneException
import com.siddhartha.walletwatcher.domain.model.onboarding.PhoneSmsResponse
import com.siddhartha.walletwatcher.domain.model.onboarding.UserData
import com.siddhartha.walletwatcher.domain.repository.OnBoardingRepository
import com.siddhartha.walletwatcher.util.AppUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val application: Application, private val repository: OnBoardingRepository
) : ViewModel() {

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?>
        get() = _userData

    fun sendOtp(
        phoneNumber: String, phoneAuthOptions: Builder
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

    fun updateScreenName(uid: String, screenName: String) = viewModelScope.launch {
        repository.updateScreenNameInDatabase(uid, screenName)
    }

    fun storeUidOfCurrentUser(uid: String) = repository.saveUidOfCurrentUser(uid)
    fun setNewUserStatus(status: Boolean) = repository.setNewUserStatus(status)

    fun isNewUser(): Boolean = repository.getNewUserStatus()

    fun getCurrentLoggedInUserData() {
        val uid = repository.getUidOfCurrentUser()
        viewModelScope.launch {
            repository.getUserData(uid).collect {
                _userData.emit(it)
            }
        }
    }
}