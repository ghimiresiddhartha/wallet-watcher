package com.siddhartha.walletwatcher.common.exception

class PhoneException(private val response: String) : Exception() {
    override val message: String
        get() = response
}