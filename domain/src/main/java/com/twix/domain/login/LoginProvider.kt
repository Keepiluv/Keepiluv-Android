package com.twix.domain.login

import com.twix.domain.model.enums.LoginType

interface LoginProvider {
    val type: LoginType

    suspend fun login(): LoginResult

    suspend fun logout(): Result<Unit>
}
