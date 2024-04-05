package com.siddhartha.walletwatcher.presentation.onboarding

import android.os.Bundle
import android.view.View
import com.siddhartha.walletwatcher.BR
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.siddhartha.walletwatcher.R
import com.siddhartha.walletwatcher.callback.CallbackType
import com.siddhartha.walletwatcher.callback.RootCallback
import com.siddhartha.walletwatcher.common.PhoneException
import com.siddhartha.walletwatcher.databinding.ActivityOnBoardingBinding
import com.siddhartha.walletwatcher.domain.model.onboarding.PhoneSmsResponse
import com.siddhartha.walletwatcher.domain.model.onboarding.UserData
import com.siddhartha.walletwatcher.presentation.base.BaseActivity
import com.siddhartha.walletwatcher.presentation.onboarding.adapter.OnBoardScreensAdapter
import com.siddhartha.walletwatcher.presentation.onboarding.adapter.OnBoardScreensAdapter.Companion.FORM_SCREEN
import com.siddhartha.walletwatcher.presentation.onboarding.adapter.OnBoardScreensAdapter.Companion.PHONE_SCREEN
import com.siddhartha.walletwatcher.presentation.onboarding.adapter.OnBoardScreensAdapter.Companion.WELCOME_SCREEN
import com.siddhartha.walletwatcher.util.ext.gone
import com.siddhartha.walletwatcher.util.ext.snack
import com.siddhartha.walletwatcher.util.ext.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : BaseActivity<ActivityOnBoardingBinding, OnBoardingViewModel>(),
    RootCallback<Any> {

    private val phoneAuthOptions = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())

    private val onBoardingViewModel: OnBoardingViewModel by viewModels()
    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var screenAdapter: OnBoardScreensAdapter
    private var updatedScreenItemPosition = 0
    private var phoneNumber = ""
    override fun getLayoutId(): Int = R.layout.activity_on_boarding

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getViewModel(): OnBoardingViewModel = onBoardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        initViewAndResources()
        screenAdapter.setData(mutableListOf(WELCOME_SCREEN))
    }

    private fun initViewAndResources() {
        screenAdapter = OnBoardScreensAdapter()
        binding.rvIntroCards.apply {
            setHasFixedSize(true)
            val itemAnimator = itemAnimator as SimpleItemAnimator
            itemAnimator.supportsChangeAnimations = false
            layoutManager = LinearLayoutManager(
                this@OnBoardingActivity,
                LinearLayoutManager.HORIZONTAL, false
            )
            PagerSnapHelper().attachToRecyclerView(this)
            adapter = screenAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        recyclerView.layoutManager?.isItemPrefetchEnabled = false
                        binding.loader.loaderParent.gone()
                        hideKeyboard()
                    }
                }
            })
        }
        screenAdapter.setRootCallBack(this)
    }

    override fun onRootCallback(index: Int, data: Any?, type: CallbackType, view: View?) {
        when (type) {
            CallbackType.REDIRECTED_FROM_WELCOME_SCREEN -> {
                updatedScreenItemPosition = index + 1
                screenAdapter.addItem(PHONE_SCREEN, updatedScreenItemPosition)
                binding.rvIntroCards.smoothScrollToPosition(updatedScreenItemPosition)
            }

            CallbackType.REDIRECTED_FROM_PHONE_SCREEN -> {
                hideKeyboard()
                binding.loader.loaderParent.visible()
                phoneAuthOptions.setActivity(this@OnBoardingActivity)
                sendOtpCodeOnDevice(data, index)
            }

            CallbackType.REDIRECTED_FROM_OTP_SCREEN -> {
                handleOtpScreenEvents(data)
            }

            CallbackType.REDIRECTED_FROM_NAME_SCREEN -> {
                updateScreenName(data)
            }

            else -> {}
        }
    }

    private fun sendOtpCodeOnDevice(screenData: Any?, screenIndex: Int) {
        val result = screenData as Result<*>
        result.apply {
            when {
                isSuccess -> {
                    phoneNumber = getOrDefault("") as String
                    onBoardingViewModel.sendOtp(
                        phoneNumber,
                        phoneAuthOptions
                    ).observe(this@OnBoardingActivity) {
                        handleOtpCodeResponse(screenIndex, it)
                    }
                }

                isFailure -> {
                    try {
                        getOrThrow()
                    } catch (e: PhoneException) {
                        binding.root.snack(e.message) {}
                    }
                }
            }
        }
    }

    private fun handleOtpCodeResponse(screenIndex: Int, response: Result<PhoneSmsResponse>){
        binding.loader.loaderParent.gone()
        response.apply {
            when {
                isSuccess -> {
                    getOrDefault(null)?.apply {
                        displayOtpScreen(screenIndex, this)
                    }
                }

                isFailure -> {
                    try {
                        getOrThrow()
                    } catch (e: PhoneException) {
                        binding.root.snack(e.message) {}
                    }
                }
            }
        }
    }

    private fun displayOtpScreen(screenIndex: Int, response: PhoneSmsResponse) {
        binding.root.snack("Code has been sent to your phone.") {}
        updatedScreenItemPosition = screenIndex + 1
        screenAdapter.addItem(response, updatedScreenItemPosition)
        binding.rvIntroCards.smoothScrollToPosition(updatedScreenItemPosition)
    }

    private fun handleOtpScreenEvents(screenData: Any?){
        val result = screenData as Result<*>
        result.apply {
            when {
                isSuccess -> {
                    val response = getOrDefault("") as PhoneSmsResponse
                    if (response.message == CallbackType.VERIFY_OTP.toString()){
                        onBoardingViewModel.verifyOtp(response).observe(this@OnBoardingActivity){
                            handleOtpVerifiedResponse(it)
                        }
                    }else if (response.message == CallbackType.RE_SEND_OTP.toString()){
                        phoneAuthOptions.setForceResendingToken(response.phoneAuthDetails!!.resendToken)
                        resendOtp()
                    }
                }

                isFailure -> {
                    try {
                        getOrThrow()
                    } catch (e: PhoneException) {
                        binding.root.snack(e.message) {}
                    }
                }
            }
        }
    }

    private fun handleOtpVerifiedResponse(response: Result<UserData>){
        response.apply {
            when {
                isSuccess -> {
                    val userData = getOrDefault(null)
                    if (userData != null) {
                        displayNameScreen(userData)
                    }
                }

                isFailure -> {
                    try {
                        getOrThrow()
                    } catch (e: PhoneException) {
                        binding.root.snack(e.message) {}
                    }
                }
            }
        }
    }

    private fun displayNameScreen(userData: UserData){
        userData.phoneNumber = phoneNumber
        binding.root.snack("Otp successfully verified.") {}
        updatedScreenItemPosition = 0
        screenAdapter.setData(mutableListOf(userData))
        binding.rvIntroCards.smoothScrollToPosition(updatedScreenItemPosition)
    }

    private fun resendOtp(){
        binding.loader.loaderParent.visible()
        onBoardingViewModel.sendOtp(phoneNumber, phoneAuthOptions).observe(this){
            binding.loader.loaderParent.gone()
            it.apply {
                if (isFailure){
                    try {
                        getOrThrow()
                    }catch (e: PhoneException){
                        binding.root.snack(e.message) {}
                    }
                } else {
                    binding.root.snack("Code has been sent to your phone.") {}
                }
            }
        }
    }

    private fun updateScreenName(screenData: Any?){
        val result = screenData as Result<*>
        result.apply {
            when{
                isSuccess -> {
                    val response = getOrDefault("") as UserData
                    onBoardingViewModel.updateScreenName(response.uid.toString(), response.screenName.toString())
                    updatedScreenItemPosition = 0
                    screenAdapter.setData(mutableListOf(FORM_SCREEN))
                    binding.rvIntroCards.smoothScrollToPosition(updatedScreenItemPosition)
                }
                isFailure -> {
                    try {
                        getOrThrow()
                    } catch (e: PhoneException) {
                        binding.root.snack(e.message) {}
                    }
                }
            }
        }
    }
}