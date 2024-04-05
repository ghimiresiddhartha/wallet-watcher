package com.siddhartha.walletwatcher.common

class PhoneException(private val response: String): Exception() {
    override val message: String
        get() = response
}