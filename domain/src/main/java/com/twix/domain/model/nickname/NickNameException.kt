package com.twix.domain.model.nickname

sealed class NickNameException : Throwable() {
    data object InvalidLengthException : NickNameException()
}
