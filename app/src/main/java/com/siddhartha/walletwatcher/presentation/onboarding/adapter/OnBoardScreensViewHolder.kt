package com.siddhartha.walletwatcher.presentation.onboarding.adapter

import android.os.CountDownTimer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.siddhartha.walletwatcher.R
import com.siddhartha.walletwatcher.callback.CallbackType
import com.siddhartha.walletwatcher.callback.RootCallback
import com.siddhartha.walletwatcher.common.PhoneException
import com.siddhartha.walletwatcher.databinding.FormScreenBinding
import com.siddhartha.walletwatcher.databinding.NameScreenBinding
import com.siddhartha.walletwatcher.databinding.OtpScreenBinding
import com.siddhartha.walletwatcher.databinding.PhoneScreenBinding
import com.siddhartha.walletwatcher.databinding.WelcomeScreenBinding
import com.siddhartha.walletwatcher.domain.model.onboarding.PhoneSmsResponse
import com.siddhartha.walletwatcher.domain.model.onboarding.UserData
import com.siddhartha.walletwatcher.domain.model.onboarding.VerifiedPhoneDetails
import com.siddhartha.walletwatcher.util.ext.gone
import com.siddhartha.walletwatcher.util.ext.preventTwoClick
import com.siddhartha.walletwatcher.util.ext.visible


open class OnBoardScreensViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    class WelcomeScreenViewHolder(
        private val binding: WelcomeScreenBinding,
        private val rootCallback: RootCallback<Any>?
    ) : OnBoardScreensViewHolder(binding) {

        fun bind() {
            binding.btnExplore.setOnClickListener {
                it.preventTwoClick()
                rootCallback?.onRootCallback(
                    adapterPosition,
                    null,
                    CallbackType.REDIRECTED_FROM_WELCOME_SCREEN,
                    it
                )
            }
        }
    }

    class PhoneScreenViewHolder(
        private val binding: PhoneScreenBinding,
        private val rootCallback: RootCallback<Any>?
    ) : OnBoardScreensViewHolder(binding) {

        fun bind() {
            binding.apply {
                btnNext.setOnClickListener {
                    it.preventTwoClick()
                    val phoneNumber = etPhoneNumber.text.toString().trim()
                    if (phoneNumber.isNotEmpty() && phoneNumber.length == 10) {
                        it.gone()
                        rootCallback?.onRootCallback(
                            adapterPosition,
                            Result.success(phoneNumber),
                            CallbackType.REDIRECTED_FROM_PHONE_SCREEN,
                            it
                        )
                    } else {
                        rootCallback?.onRootCallback(
                            adapterPosition,
                            Result.failure<String>(PhoneException(it.context.getString(R.string.invalid_input))),
                            CallbackType.REDIRECTED_FROM_PHONE_SCREEN,
                            it
                        )
                    }
                }
            }
        }
    }

    class OtpScreenViewHolder(
        val binding: OtpScreenBinding,
        private val rootCallback: RootCallback<Any>?
    ) : OnBoardScreensViewHolder(binding) {

        fun bind(phoneSmsResponse: PhoneSmsResponse) {
            val countDownTimer = setupCountdownTimer()
            with(binding) {
                tvTimer.apply {
                    tag = 0
                    countDownTimer.start()
                    setOnClickListener {
                        it.preventTwoClick()
                        phoneSmsResponse.message = CallbackType.RE_SEND_OTP.toString()
                        if (it.tag == 1) {
                            countDownTimer.start()
                            btnVerify.visible()
                            rootCallback?.onRootCallback(
                                adapterPosition,
                                Result.success(phoneSmsResponse),
                                CallbackType.REDIRECTED_FROM_OTP_SCREEN,
                                it
                            )
                        }
                    }
                }

                btnVerify.setOnClickListener {
                    it.preventTwoClick()
                    val otpCode = etOtpCode.text.toString().trim()
                    if (otpCode.isNotEmpty() && otpCode.length == 6) {
                        countDownTimer.cancel()
                        it.gone()
                        phoneSmsResponse.message = CallbackType.VERIFY_OTP.toString()
                        if (phoneSmsResponse.phoneSmsDetails == null) {
                            phoneSmsResponse.phoneSmsDetails = VerifiedPhoneDetails(
                                otpCode,
                                "",
                                ""
                            )
                        }
                        rootCallback?.onRootCallback(
                            adapterPosition,
                            Result.success(phoneSmsResponse),
                            CallbackType.REDIRECTED_FROM_OTP_SCREEN,
                            it
                        )
                    } else {
                        rootCallback?.onRootCallback(
                            adapterPosition,
                            Result.failure<String>(PhoneException(it.context.getString(R.string.invalid_input))),
                            CallbackType.REDIRECTED_FROM_OTP_SCREEN,
                            it
                        )
                    }
                }
            }

        }

        private fun setupCountdownTimer(): CountDownTimer =
            object : CountDownTimer(60 * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = (millisUntilFinished / 1000).toInt()
                    if (seconds < 10)
                        binding.tvTimer.text = "00:0"
                    else
                        binding.tvTimer.text = "00:$seconds"
                }

                override fun onFinish() {
                    binding.tvTimer.text = binding.root.context.getString(R.string.resend_code)
                    binding.tvTimer.tag = 1
                }
            }
    }

    class NameScreenViewHolder(
        private val binding: NameScreenBinding,
        private val rootCallback: RootCallback<Any>?
    ) : OnBoardScreensViewHolder(binding) {

        fun bind(userData: UserData) {
            binding.apply {
                tvPhoneNumber.text = userData.phoneNumber
                btnNext.setOnClickListener {
                    val screenName = etScreenName.text.toString().trim()
                    if (screenName.isNotEmpty()) {
                        userData.screenName = screenName
                        rootCallback?.onRootCallback(
                            adapterPosition,
                            Result.success(userData),
                            CallbackType.REDIRECTED_FROM_NAME_SCREEN,
                            it
                        )
                    } else {
                        rootCallback?.onRootCallback(
                            adapterPosition,
                            Result.failure<String>(PhoneException(it.context.getString(R.string.invalid_input))),
                            CallbackType.REDIRECTED_FROM_NAME_SCREEN,
                            it
                        )
                    }
                }
            }
        }
    }

    class FormScreenViewHolder(
        private val binding: FormScreenBinding,
        private val rootCallback: RootCallback<Any>?
    ) : OnBoardScreensViewHolder(binding) {

        fun bind(){

        }
    }
}