package com.twix.domain.model.invitecode

sealed class InviteCodeException : Throwable() {
    data object InvalidLengthException : InviteCodeException()
}
