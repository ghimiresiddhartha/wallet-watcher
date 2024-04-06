package com.siddhartha.walletwatcher.presentation.onboarding.adapter

import android.os.CountDownTimer
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.siddhartha.walletwatcher.R
import com.siddhartha.walletwatcher.common.callback.CallbackType
import com.siddhartha.walletwatcher.common.callback.RootCallback
import com.siddhartha.walletwatcher.common.exception.PhoneException
import com.siddhartha.walletwatcher.databinding.FormScreenBinding
import com.siddhartha.walletwatcher.databinding.NameScreenBinding
import com.siddhartha.walletwatcher.databinding.OtpScreenBinding
import com.siddhartha.walletwatcher.databinding.PhoneScreenBinding
import com.siddhartha.walletwatcher.databinding.WelcomeScreenBinding
import com.siddhartha.walletwatcher.domain.model.onboarding.FormData
import com.siddhartha.walletwatcher.domain.model.onboarding.PhoneSmsResponse
import com.siddhartha.walletwatcher.domain.model.onboarding.UserData
import com.siddhartha.walletwatcher.domain.model.onboarding.VerifiedPhoneDetails
import com.siddhartha.walletwatcher.util.ext.gone
import com.siddhartha.walletwatcher.util.ext.hideKeyboard
import com.siddhartha.walletwatcher.util.ext.preventTwoClick
import com.siddhartha.walletwatcher.util.ext.visible
import soup.neumorphism.NeumorphCardView


open class OnBoardScreensViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    class WelcomeScreenViewHolder(
        private val binding: WelcomeScreenBinding, private val rootCallback: RootCallback<Any>?
    ) : OnBoardScreensViewHolder(binding) {

        fun bind() {
            binding.btnExplore.setOnClickListener {
                it.preventTwoClick()
                it.gone()
                rootCallback?.onRootCallback(
                    adapterPosition, null, CallbackType.REDIRECTED_FROM_WELCOME_SCREEN, it
                )
            }
        }
    }

    class PhoneScreenViewHolder(
        private val binding: PhoneScreenBinding, private val rootCallback: RootCallback<Any>?
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
        val binding: OtpScreenBinding, private val rootCallback: RootCallback<Any>?
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
                        if (tag == 1) {
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
                        phoneSmsResponse.message = CallbackType.VERIFY_OTP.toString()
                        if (phoneSmsResponse.phoneSmsDetails == null) {
                            phoneSmsResponse.phoneSmsDetails = VerifiedPhoneDetails(
                                otpCode, "", ""
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
                    if (seconds < 10) binding.tvTimer.text = "00:0$seconds"
                    else binding.tvTimer.text = "00:$seconds"
                }

                override fun onFinish() {
                    binding.tvTimer.text = binding.root.context.getString(R.string.resend_code)
                    binding.tvTimer.tag = 1
                }
            }
    }

    class NameScreenViewHolder(
        private val binding: NameScreenBinding, private val rootCallback: RootCallback<Any>?
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
        private val binding: FormScreenBinding, private val rootCallback: RootCallback<Any>?
    ) : OnBoardScreensViewHolder(binding) {

        fun bind() {
            with(binding) {
                etMonthlyIncome.hideKeyboard()
                ivExpandArrowFood.setOnClickListener {
                    it.hideKeyboard()
                    cvEntertainmentContainer.gone()
                    cvTransportationContainer.gone()
                    expandCategory(ivExpandArrowFood, cvFoodContainer, cvFoodCard)
                }

                ivExpandArrowEntertainment.setOnClickListener {
                    it.hideKeyboard()
                    cvFoodContainer.gone()
                    cvTransportationContainer.gone()
                    expandCategory(
                        ivExpandArrowEntertainment, cvEntertainmentContainer, cvEntertainmentCard
                    )
                }

                ivExpandArrowTransportation.setOnClickListener {
                    it.hideKeyboard()
                    cvEntertainmentContainer.gone()
                    cvFoodContainer.gone()
                    expandCategory(
                        ivExpandArrowTransportation, cvTransportationContainer, cvTransportationCard
                    )
                }

                btnCalculate.setOnClickListener {
                    val monthlyIncome = etMonthlyIncome.text.toString().trim()
                    val foodAmount = etFoodAmount.text.toString().trim()
                    val entertainmentAmount = etEntertainmentAmount.text.toString().trim()
                    val transportationAmount = etTransportationAmount.text.toString().trim()

                    if (monthlyIncome.isNotEmpty()) {
                        if (foodAmount.isNotEmpty() && entertainmentAmount.isNotEmpty() && transportationAmount.isNotEmpty()) {
                            val formData = FormData(
                                monthlyIncome.toInt(),
                                foodAmount.toInt(),
                                entertainmentAmount.toInt(),
                                transportationAmount.toInt()
                            )

                            rootCallback?.onRootCallback(
                                adapterPosition,
                                Result.success(formData),
                                CallbackType.REDIRECTED_FROM_FORM_SCREEN,
                                it
                            )

                        } else {
                            rootCallback?.onRootCallback(
                                adapterPosition,
                                Result.failure<String>(PhoneException(it.context.getString(R.string.field_error))),
                                CallbackType.REDIRECTED_FROM_FORM_SCREEN,
                                it
                            )
                        }
                    } else {
                        rootCallback?.onRootCallback(
                            adapterPosition,
                            Result.failure<String>(PhoneException(it.context.getString(R.string.invalid_input))),
                            CallbackType.REDIRECTED_FROM_FORM_SCREEN,
                            it
                        )
                    }
                }
            }
        }

        private fun expandCategory(
            arrow: ImageView, categoryCardView: NeumorphCardView, parentView: CardView
        ) {
            if (!categoryCardView.isVisible) {
                arrow.rotation = 270f
                TransitionManager.beginDelayedTransition(parentView, AutoTransition())
                categoryCardView.visible()
            } else {
                arrow.rotation = 0f
                categoryCardView.gone()
            }
        }
    }
}