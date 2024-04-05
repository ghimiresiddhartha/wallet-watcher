package com.siddhartha.walletwatcher.data.model.onboarding

import com.google.firebase.auth.PhoneAuthProvider

data class PhoneAuthDetails(
    val verificationId: String?,
    val resendToken: PhoneAuthProvider.ForceResendingToken
)
