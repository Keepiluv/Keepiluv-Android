package com.twix.login.contract

import com.twix.domain.login.LoginResult
import com.twix.ui.base.Intent

sealed interface LoginIntent : Intent {
    data class Login(
        val result: LoginResult,
    ) : LoginIntent
}
