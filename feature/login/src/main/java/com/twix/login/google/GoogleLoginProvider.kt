package com.twix.login.google

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.twix.domain.login.LoginProvider
import com.twix.domain.login.LoginResult
import com.twix.domain.model.enums.LoginType
import com.twix.login.BuildConfig

/**
 * Google 로그인 Provider 구현체.
 *
 * [LoginProvider] 인터페이스를 구현하여
 * UI가 Google SDK에 직접 의존하지 않도록 캡슐화
 *
 * ### 로그인 흐름
 * 1. silent sign-in 시도 (기존 로그인 계정 자동 인증)
 * 2. 실패 시 명시적 sign-in (계정 선택 UI 표시)
 * 3. ID Token 추출 후 서버 인증에 전달
 *
 */
class GoogleLoginProvider(
    private val context: Context,
) : LoginProvider {
    override val type: LoginType = LoginType.GOOGLE

    /** Android Credential API 진입점 */
    private val credentialManager = CredentialManager.create(context)

    /** Google Cloud Console에서 발급된 Web Client ID */
    private val serverClientId = BuildConfig.GOOGLE_CLIENT_ID

    /**
     * 기존 로그인된 계정을 자동 인증하기 위한 옵션 (silent login)
     */
    private val googleIdOption =
        GetGoogleIdOption
            .Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()

    /**
     * 사용자에게 계정 선택 UI를 보여주는 명시적 로그인 옵션 (explicit login)
     */
    private val signInOption =
        GetSignInWithGoogleOption
            .Builder(serverClientId)
            .build()

    /** silent 로그인 요청 */
    private val silentRequest = buildRequest(googleIdOption)

    /** 명시적 로그인 요청 */
    private val explicitRequest = buildRequest(signInOption)

    override suspend fun login(): LoginResult =
        when (val result = getGoogleCredentialResult()) {
            is GoogleCredentialResult.Success ->
                LoginResult.Success(result.idToken, type)

            GoogleCredentialResult.Cancel ->
                LoginResult.Cancel

            is GoogleCredentialResult.Failure ->
                LoginResult.Failure(result.exception)
        }

    /**
     * Google Credential 상태를 초기화하여 로그아웃 처리
     */
    override suspend fun logout(): Result<Unit> =
        runCatching {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        }

    /**
     * Google 인증 전체 프로세스를 수행
     *
     *  1. silent 요청 시도
     *  2. 실패 시 명 요청 수행
     *
     * @return Google 전용 결과 타입
     */
    private suspend fun getGoogleCredentialResult(): GoogleCredentialResult {
        val silent = request(silentRequest)

        return if (silent is GoogleCredentialResult.Failure) {
            request(explicitRequest)
        } else {
            silent
        }
    }

    /**
     * Credential 요청을 실제로 수행하고 결과 파싱
     *
     * @param request CredentialRequest 객체
     * @return GoogleCredentialResult
     */
    private suspend fun request(request: GetCredentialRequest): GoogleCredentialResult =
        runCatching {
            credentialManager.getCredential(context, request)
        }.fold(
            onSuccess = { handleResponse(it) },
            onFailure = { handleException(it) },
        )

    /**
     * Credential 응답에서 Google ID Token을 추출
     *
     * - CustomCredential 타입 검증
     * - GoogleIdTokenCredential 파싱
     *
     * @throws NoCredentialException 유효한 Credential이 아닌 경우
     */
    private fun handleResponse(response: GetCredentialResponse): GoogleCredentialResult {
        val credential = response.credential

        if (credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return GoogleCredentialResult.Failure(NoCredentialException())
        }

        return try {
            val token =
                GoogleIdTokenCredential.createFrom(credential.data).idToken
            GoogleCredentialResult.Success(token)
        } catch (e: Throwable) {
            GoogleCredentialResult.Failure(e)
        }
    }

    /**
     * Credential 요청 중 발생한 예외를 Google 전용 Result로 변환
     *
     * - Cancellation → Cancel
     * - CredentialException → Suspending (다음 단계 진행 가능)
     * - 기타 → Failure
     */
    private fun handleException(e: Throwable): GoogleCredentialResult =
        when (e) {
            is GetCredentialCancellationException -> GoogleCredentialResult.Cancel
            is GetCredentialException -> GoogleCredentialResult.Failure(e)
            else -> GoogleCredentialResult.Failure(e)
        }

    /**
     * CredentialOption으로부터 GetCredentialRequest 생성
     */
    private fun buildRequest(option: CredentialOption) =
        GetCredentialRequest
            .Builder()
            .addCredentialOption(option)
            .build()
}
