package com.twix.login

import com.twix.domain.login.LoginProvider
import com.twix.domain.model.enums.LoginType

class LoginProviderFactory(
    private val providers: Map<LoginType, LoginProvider>,
) {
    operator fun get(type: LoginType): LoginProvider =
        providers[type]
            ?: throw IllegalArgumentException(UNSUPPORTED_LOGIN_TYPE.format(type))

    companion object {
        private const val UNSUPPORTED_LOGIN_TYPE = "Unsupported login type: %s"
    }
}
