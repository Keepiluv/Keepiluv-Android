package com.twix.login.kakao

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.twix.domain.login.LoginProvider
import com.twix.domain.login.LoginResult
import com.twix.domain.model.enums.LoginType
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class KakaoLoginProvider(
    private val context: Context,
) : LoginProvider {
    override val type: LoginType = LoginType.KAKAO

    override suspend fun login(): LoginResult {
        if (isKakaoTalkAvailable()) return loginWithTalk()
        return loginWithAccount()
    }

    override suspend fun logout(): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    continuation.resume(Result.failure(error))
                } else {
                    continuation.resume(Result.success(Unit))
                }
            }
        }

    private fun isKakaoTalkAvailable(): Boolean = UserApiClient.instance.isKakaoTalkLoginAvailable(context)

    private suspend fun loginWithTalk(): LoginResult =
        suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                when {
                    token != null -> continuation.resume(success(token))
                    error is ClientError &&
                        error.reason == ClientErrorCause.Cancelled -> {
                        continuation.resume(LoginResult.Cancel)
                    }

                    else -> resumeWithAccountLogin(continuation)
                }
            }
        }

    private suspend fun loginWithAccount(): LoginResult =
        suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                when {
                    token != null -> continuation.resume(success(token))
                    error != null -> continuation.resume(LoginResult.Failure(error))
                }
            }
        }

    private fun success(token: OAuthToken): LoginResult = LoginResult.Success(token.idToken.toString(), LoginType.KAKAO)

    private fun resumeWithAccountLogin(continuation: Continuation<LoginResult>) {
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            when {
                token != null -> continuation.resume(success(token))
                error != null -> continuation.resume(LoginResult.Failure(error))
            }
        }
    }
}
