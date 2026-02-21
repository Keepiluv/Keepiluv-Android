package com.twix.domain.login

import com.twix.domain.model.enums.LoginType

interface LoginProvider {
    suspend fun login(): LoginResult

    suspend fun logout(): Result<Unit>
}
