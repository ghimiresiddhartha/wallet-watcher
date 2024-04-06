package com.siddhartha.walletwatcher.data.mapper

import com.siddhartha.walletwatcher.data.model.onboarding.PhoneAuthDetails
import com.siddhartha.walletwatcher.data.model.onboarding.UserData
import com.siddhartha.walletwatcher.data.model.onboarding.VerifiedPhoneDetails

fun VerifiedPhoneDetails.toPhoneSmsDetails() =
    com.siddhartha.walletwatcher.domain.model.onboarding.VerifiedPhoneDetails(
        smsCode, null, null
    )

fun PhoneAuthDetails.toPhoneAuthDetails() =
    com.siddhartha.walletwatcher.domain.model.onboarding.PhoneAuthDetails(
        verificationId, resendToken
    )

fun UserData.toUserData() = com.siddhartha.walletwatcher.domain.model.onboarding.UserData(
    uid, screenName, null
)