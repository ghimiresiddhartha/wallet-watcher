package com.siddhartha.walletwatcher.domain.model.onboarding

data class VerifiedPhoneDetails(
    var smsCode: String?, val signInMethod: String?, val provider: String?
)
