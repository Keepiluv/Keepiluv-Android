package com.twix.data.repository

import com.twix.domain.model.enums.LoginType
import com.twix.domain.repository.AuthRepository
import com.twix.network.execute.safeApiCall
import com.twix.network.model.request.LoginRequest
import com.twix.network.service.AuthService
import com.twix.result.AppResult
import com.twix.token.TokenProvider

class DefaultAuthRepository(
    private val service: AuthService,
    private val tokenProvider: TokenProvider,
) : AuthRepository {
    override suspend fun login(
        idToken: String,
        type: LoginType,
    ) {
        val response =
            when (type) {
                LoginType.GOOGLE -> service.googleLogin(LoginRequest(idToken))
                LoginType.KAKAO -> return
            }

        tokenProvider.saveToken(response.accessToken, response.refreshToken)
    }

    override suspend fun logout(): AppResult<Unit> =
        safeApiCall {
            service.logout()
            tokenProvider.clear()
        }

    override suspend fun withdrawAccount() =
        safeApiCall {
            service.withdrawAccount()
            tokenProvider.clear()
        }
}
