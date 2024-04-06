package com.siddhartha.walletwatcher.presentation.onboarding

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import com.siddhartha.walletwatcher.BR
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.siddhartha.walletwatcher.R
import com.siddhartha.walletwatcher.common.callback.CallbackType
import com.siddhartha.walletwatcher.common.callback.RootCallback
import com.siddhartha.walletwatcher.common.exception.PhoneException
import com.siddhartha.walletwatcher.databinding.ActivityOnBoardingBinding
import com.siddhartha.walletwatcher.domain.model.onboarding.FormData
import com.siddhartha.walletwatcher.domain.model.onboarding.PhoneSmsResponse
import com.siddhartha.walletwatcher.domain.model.onboarding.UserData
import com.siddhartha.walletwatcher.presentation.base.BaseActivity
import com.siddhartha.walletwatcher.presentation.onboarding.adapter.OnBoardScreensAdapter
import com.siddhartha.walletwatcher.presentation.onboarding.adapter.OnBoardScreensAdapter.Companion.FORM_SCREEN
import com.siddhartha.walletwatcher.presentation.onboarding.adapter.OnBoardScreensAdapter.Companion.PHONE_SCREEN
import com.siddhartha.walletwatcher.presentation.onboarding.adapter.OnBoardScreensAdapter.Companion.WELCOME_SCREEN
import com.siddhartha.walletwatcher.util.AppConstant.FORM_DATA
import com.siddhartha.walletwatcher.util.AppConstant.USER_DATA
import com.siddhartha.walletwatcher.util.ext.gone
import com.siddhartha.walletwatcher.util.ext.openHomeActivity
import com.siddhartha.walletwatcher.util.ext.snack
import com.siddhartha.walletwatcher.util.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnBoardingActivity : BaseActivity<ActivityOnBoardingBinding, OnBoardingViewModel>(),
    RootCallback<Any> {

    private val phoneAuthOptions = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())

    private val onBoardingViewModel: OnBoardingViewModel by viewModels()
    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var screenAdapter: OnBoardScreensAdapter
    private var updatedScreenItemPosition = 0
    private var phoneNumber = ""
    private var screenName = ""
    private var uid = ""

    override fun getLayoutId(): Int = R.layout.activity_on_boarding

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getViewModel(): OnBoardingViewModel = onBoardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        showProgressDialog()
        onBoardingViewModel.getCurrentLoggedInUserData()
        initViewAndResources()
        manageUserSession()
    }

    private fun manageUserSession() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                onBoardingViewModel.userData.collect { userData ->
                    finishProgressDialog()
                    if (userData != null) {
                        uid = userData.uid.toString()
                        if (userData.screenName != "null") {
                            screenName = userData.screenName.toString()
                            openFormScreen()
                        } else {
                            openNameScreen(
                                UserData(
                                    userData.uid, null, phoneNumber
                                )
                            )
                        }
                    } else {
                        if (onBoardingViewModel.isNewUser()) {
                            finishProgressDialog()
                            openWelcomeScreen()
                        }
                    }
                }
            }
        }
    }

    private fun initViewAndResources() {
        screenAdapter = OnBoardScreensAdapter()
        binding.rvIntroCards.apply {
            setHasFixedSize(true)
            val itemAnimator = itemAnimator as SimpleItemAnimator
            itemAnimator.supportsChangeAnimations = false
            layoutManager = LinearLayoutManager(
                this@OnBoardingActivity, LinearLayoutManager.HORIZONTAL, false
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
                openPhoneScreen()
            }

            CallbackType.REDIRECTED_FROM_PHONE_SCREEN -> {
                hideKeyboard()
                updatedScreenItemPosition = index + 1
                initOtpConfiguration(data)
            }

            CallbackType.REDIRECTED_FROM_OTP_SCREEN -> {
                hideKeyboard()
                updatedScreenItemPosition = index
                manageOtpScreenEvents(data)
            }

            CallbackType.REDIRECTED_FROM_NAME_SCREEN -> {
                updatedScreenItemPosition = 0
                manageNameScreenEvents(data)
            }

            CallbackType.REDIRECTED_FROM_FORM_SCREEN -> {
                manageFormScreenEvents(data)
            }

            else -> {}
        }
    }

    private fun openWelcomeScreen() {
        if (screenAdapter.getItemList().isEmpty()) {
            screenAdapter.setData(mutableListOf(WELCOME_SCREEN))
        } else {
            binding.rvIntroCards.smoothScrollToPosition(updatedScreenItemPosition)
            binding.loader.loaderParent.visible()
        }
    }

    private fun openPhoneScreen() {
        screenAdapter.addItem(PHONE_SCREEN, updatedScreenItemPosition)
        binding.rvIntroCards.smoothScrollToPosition(updatedScreenItemPosition)
    }

    private fun openOtpScreen(response: PhoneSmsResponse) {
        try {
            val item = screenAdapter.getItemList().elementAt(updatedScreenItemPosition)
            if (item is PhoneSmsResponse) {
                binding.rvIntroCards.smoothScrollToPosition(updatedScreenItemPosition)
            } else {
                screenAdapter.addItem(response, updatedScreenItemPosition)
                binding.rvIntroCards.smoothScrollToPosition(updatedScreenItemPosition)
            }
        } catch (e: IndexOutOfBoundsException) {
            screenAdapter.addItem(response, updatedScreenItemPosition)
            binding.rvIntroCards.smoothScrollToPosition(updatedScreenItemPosition)
        }
    }

    private fun openNameScreen(userData: UserData) {
        onBoardingViewModel.setNewUserStatus(false)
        updatedScreenItemPosition = 0
        userData.phoneNumber = phoneNumber
        screenAdapter.setData(mutableListOf(userData))
        binding.rvIntroCards.smoothScrollToPosition(updatedScreenItemPosition)
    }

    private fun openFormScreen() {
        updatedScreenItemPosition = 0
        screenAdapter.setData(mutableListOf(FORM_SCREEN))
        binding.rvIntroCards.smoothScrollToPosition(updatedScreenItemPosition)
    }

    private fun initOtpConfiguration(screenData: Any?) {
        val result = screenData as Result<*>
        binding.loader.loaderParent.visible()
        phoneAuthOptions.setActivity(this@OnBoardingActivity)
        result.apply {
            when {
                isSuccess -> {
                    phoneNumber = getOrDefault("") as String
                    sendOtpCodeOnDevice()
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

    private fun sendOtpCodeOnDevice() {
        onBoardingViewModel.sendOtp(
            phoneNumber, phoneAuthOptions
        ).observe(this@OnBoardingActivity) {
            binding.loader.loaderParent.gone()
            it.apply {
                when {
                    isSuccess -> {
                        getOrDefault(null)?.apply {
                            if (this.phoneAuthDetails?.resendToken != null) {
                                binding.root.snack("Code has been sent to your phone.") {}
                                openOtpScreen(this)
                            }
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
    }

    private fun manageOtpScreenEvents(screenData: Any?) {
        val result = screenData as Result<*>
        result.apply {
            when {
                isSuccess -> {
                    binding.loader.loaderParent.visible()
                    val response = getOrDefault("") as PhoneSmsResponse
                    if (response.message == CallbackType.VERIFY_OTP.toString()) {
                        updatedScreenItemPosition = 0
                        verifyOtpCode(response)
                    } else if (response.message == CallbackType.RE_SEND_OTP.toString()) {
                        resendOtpCode(response)
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

    private fun verifyOtpCode(response: PhoneSmsResponse) {
        onBoardingViewModel.verifyOtp(response).observe(this@OnBoardingActivity) {
            binding.loader.loaderParent.gone()
            it.apply {
                when {
                    isSuccess -> {
                        val userData = getOrDefault(null)
                        onBoardingViewModel.storeUidOfCurrentUser(userData?.uid.toString())
                        if (userData != null) {
                            binding.root.snack("Otp successfully verified.") {}
                            openNameScreen(userData)
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
    }

    private fun resendOtpCode(response: PhoneSmsResponse) {
        response.phoneAuthDetails?.resendToken?.let {
            phoneAuthOptions.setForceResendingToken(it)
        }
        sendOtpCodeOnDevice()
    }

    private fun manageNameScreenEvents(screenData: Any?) {
        val result = screenData as Result<*>
        result.apply {
            when {
                isSuccess -> {
                    val response = getOrDefault("") as UserData
                    screenName = response.screenName.toString()
                    updateScreenNameOnDatabase(response)
                    openFormScreen()
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

    private fun updateScreenNameOnDatabase(response: UserData) {
        onBoardingViewModel.updateScreenName(
            response.uid.toString(), response.screenName.toString()
        )
    }

    private fun manageFormScreenEvents(screenData: Any?) {
        val result = screenData as Result<*>
        result.apply {
            when {
                isSuccess -> {
                    val formData = getOrDefault("") as FormData
                    val userData = UserData(null, screenName, phoneNumber)
                    openHomeActivity {
                        putParcelable(USER_DATA, userData)
                        putParcelable(FORM_DATA, formData)
                    }
                    this@OnBoardingActivity.finish()
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val item = screenAdapter.getItemList().elementAt(updatedScreenItemPosition)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && item == FORM_SCREEN) {
            binding.toolbar.gone()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT && item == FORM_SCREEN) {
            binding.toolbar.visible()
        }
    }
}