package com.twix.login.kakao

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.twix.domain.login.LoginResult
import com.twix.domain.model.enums.LoginType
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class KakaoLoginProvider {
    suspend fun login(context: Context): LoginResult {
        if (isKakaoTalkAvailable(context)) return loginWithTalk(context)
        return loginWithAccount(context)
    }

    suspend fun logout(): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    continuation.resume(Result.failure(error))
                } else {
                    continuation.resume(Result.success(Unit))
                }
            }
        }

    private fun isKakaoTalkAvailable(context: Context): Boolean = UserApiClient.instance.isKakaoTalkLoginAvailable(context)

    private suspend fun loginWithTalk(context: Context): LoginResult =
        suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                when {
                    token != null -> {
                        continuation.resume(success(token))
                    }
                    error is ClientError &&
                        error.reason == ClientErrorCause.Cancelled -> {
                        continuation.resume(LoginResult.Cancel)
                    }

                    else -> {
                        loginWithKakaoAccount(context, continuation)
                    }
                }
            }
        }

    private suspend fun loginWithAccount(context: Context): LoginResult =
        suspendCancellableCoroutine { continuation ->
            loginWithKakaoAccount(context, continuation)
        }

    private fun loginWithKakaoAccount(
        context: Context,
        continuation: CancellableContinuation<LoginResult>,
    ) {
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            if (!continuation.isActive) return@loginWithKakaoAccount
            handleLoginCallback(token, error, continuation)
        }
    }

    private fun handleLoginCallback(
        token: OAuthToken?,
        error: Throwable?,
        continuation: CancellableContinuation<LoginResult>,
    ) {
        when {
            token != null -> {
                continuation.resume(success(token))
            }
            error != null -> {
                continuation.resume(LoginResult.Failure(error))
            }
            else -> {
                continuation.resume(
                    LoginResult.Failure(
                        IllegalStateException(UNEXPECTED_STATE_ERROR_MESSAGE),
                    ),
                )
            }
        }
    }

    private fun success(token: OAuthToken): LoginResult {
        val idToken =
            requireNotNull(token.idToken) {
                return LoginResult.Failure(IllegalStateException(ID_TOKEN_NULL_ERROR_MESSAGE))
            }
        return LoginResult.Success(idToken, LoginType.KAKAO)
    }

    companion object {
        private const val ID_TOKEN_NULL_ERROR_MESSAGE =
            "idToken is null. Ensure OpenID Connect is enabled in Kakao developer console."
        private const val UNEXPECTED_STATE_ERROR_MESSAGE =
            "Unexpected: both token and error are null"
    }
}
