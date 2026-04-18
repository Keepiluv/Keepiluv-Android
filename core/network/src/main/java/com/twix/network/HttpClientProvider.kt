package com.twix.network

import com.twix.network.model.request.RefreshRequest
import com.twix.network.service.AuthService
import com.twix.token.TokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal object HttpClientProvider {
    fun createHttpClient(
        tokenProvider: TokenProvider,
        authService: Lazy<AuthService>,
        baseUrl: String,
        isDebug: Boolean,
    ): HttpClient =
        HttpClient {
            expectSuccess = true

            configureContentNegotiation(isDebug)
            configureLogging(isDebug)
            configureTimeout()
            configureDefaultRequest(baseUrl)
            configureAuth(tokenProvider, authService, baseUrl)
        }

    private fun HttpClientConfig<*>.configureContentNegotiation(isDebug: Boolean) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = isDebug
                },
            )
        }
    }

    private fun HttpClientConfig<*>.configureLogging(isDebug: Boolean) {
        install(Logging) {
            level =
                if (isDebug) {
                    LogLevel.BODY
                } else {
                    LogLevel.NONE
                }
            logger = Logger.ANDROID

            sanitizeHeader { header -> header == SANITIZE_HEADER }
        }
    }

    private fun HttpClientConfig<*>.configureTimeout() {
        install(HttpTimeout) {
            connectTimeoutMillis = TIMEOUT_MILLIS
            requestTimeoutMillis = TIMEOUT_MILLIS
            socketTimeoutMillis = TIMEOUT_MILLIS
        }
    }

    private fun HttpClientConfig<*>.configureDefaultRequest(baseUrl: String) {
        defaultRequest {
            url(baseUrl)
            contentType(ContentType.Application.Json)
        }
    }

    /**
     * Ktor [Auth] Bearer 플러그인을 설정한다.
     *
     * ### 역할
     * - 모든 요청에 AccessToken 자동 첨부
     * - 401 발생 시 RefreshToken으로 토큰 재발급
     * - 재발급 성공 시 TokenProvider에 토큰 갱신 후 재요청
     *
     * ### 왜 Lazy<AuthService> 인가?
     * HttpClient ↔ AuthService 간 순환 의존성을 방지하기 위함.
     *
     * AuthService 내부에서 HttpClient를 사용하고,
     * HttpClient 설정 시 AuthService가 필요하기 때문에
     * 즉시 생성하면 DI 순환참조가 발생한다.
     *
     * Lazy 로 지연 생성하여 실제 refresh 시점에만 인스턴스를 획득한다.
     *
     */
    private fun HttpClientConfig<*>.configureAuth(
        tokenProvider: TokenProvider,
        lazyAuthService: Lazy<AuthService>,
        baseUrl: String,
    ) {
        install(Auth) {
            bearer {
                cacheTokens = false

                /**
                 * [loadTokens]
                 * - 현재 저장된 access/refresh token을 읽어 Authorization 헤더에 추가
                 * */
                loadTokens {
                    val accessToken = tokenProvider.accessToken
                    val refreshToken = tokenProvider.refreshToken
                    BearerTokens(accessToken, refreshToken)
                }

                /**
                 * [refreshTokens]
                 *  - 401 Unauthorized 발생 시 호출
                 *  - AuthService.refresh() API 호출
                 *  - 새 토큰을 발급받아 TokenProvider에 저장
                 *  - BearerTokens 반환 → 원 요청 자동 재시도
                 * **/
                refreshTokens {
                    val refreshToken = tokenProvider.refreshToken
                    val request = RefreshRequest(refreshToken)
                    val apiService = lazyAuthService.value
                    val (newAccessToken, newRefreshToken) = apiService.refresh(request)

                    tokenProvider.saveToken(newAccessToken, newRefreshToken)
                    BearerTokens(newAccessToken, newRefreshToken)
                }

                /**
                 * [sendWithoutRequest]
                 *  - refresh API 호출 시에는 토큰을 붙이지 않음
                 *  - refresh 요청 자체가 다시 refresh 되는 무한 루프 방지 목적
                 * **/
                sendWithoutRequest { request: HttpRequestBuilder ->
                    val requestUrl: String = request.url.toString()

                    requestUrl.startsWith(baseUrl) &&
                        !requestUrl.endsWith(AUTH_REFRESH_ENDPOINT)
                }
            }
        }
    }

    private const val SANITIZE_HEADER = "Authorization"
    private const val TIMEOUT_MILLIS = 30_000L

    private const val AUTH_REFRESH_ENDPOINT = "/auth/refresh"
}
