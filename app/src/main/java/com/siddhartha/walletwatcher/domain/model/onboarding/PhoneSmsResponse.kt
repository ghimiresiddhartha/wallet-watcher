package com.siddhartha.walletwatcher.domain.model.onboarding

data class PhoneSmsResponse(
    var phoneSmsDetails: VerifiedPhoneDetails?,
    var phoneAuthDetails: PhoneAuthDetails?,
    var message: String?
)
