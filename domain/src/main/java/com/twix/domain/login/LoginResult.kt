package com.twix.domain.login

import com.twix.domain.model.enums.LoginType

sealed interface LoginResult {
    data class Success(
        val idToken: String,
        val type: LoginType,
    ) : LoginResult

    data object Cancel : LoginResult

    data class Failure(
        val throwable: Throwable?,
    ) : LoginResult
}
