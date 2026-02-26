package com.twix.data.repository

import com.twix.domain.model.enums.LoginType
import com.twix.domain.repository.AuthRepository
import com.twix.network.execute.safeApiCall
import com.twix.network.model.request.LoginRequest
import com.twix.network.model.request.RefreshRequest
import com.twix.network.service.AuthService
import com.twix.result.AppError
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
        val request = LoginRequest(idToken)
        val response =
            when (type) {
                LoginType.GOOGLE -> service.googleLogin(request)
                LoginType.KAKAO -> service.kakaoLogin(request)
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

    override suspend fun refreshAccessToken(): AppResult<Unit> {
        val refreshToken = tokenProvider.loadRefreshToken()

        if (refreshToken.isBlank()) {
            return AppResult.Error(AppError.Auth.Unauthorized(401, null, "refresh token이 존재하지 않음", null))
        }

        return safeApiCall {
            val response = service.refresh(RefreshRequest(refreshToken))
            tokenProvider.saveToken(response.accessToken, response.refreshToken)
        }
    }
}
