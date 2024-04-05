package com.siddhartha.walletwatcher.data.model.onboarding

data class PhoneSmsResponse(
    var phoneSmsDetails: VerifiedPhoneDetails?,
    var phoneAuthDetails: PhoneAuthDetails?,
    var message: String?
)
