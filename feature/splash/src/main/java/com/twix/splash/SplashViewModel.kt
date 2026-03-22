package com.twix.splash

import androidx.lifecycle.viewModelScope
import com.twix.domain.model.OnboardingStatus
import com.twix.domain.repository.AuthRepository
import com.twix.domain.repository.OnBoardingRepository
import com.twix.result.AppResult
import com.twix.splash.contract.SplashSideEffect
import com.twix.ui.base.BaseViewModel
import com.twix.ui.base.EmptyIntent
import com.twix.ui.base.EmptyState
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class SplashViewModel(
    private val authRepository: AuthRepository,
    private val onBoardingRepository: OnBoardingRepository,
) : BaseViewModel<EmptyState, EmptyIntent, SplashSideEffect>(EmptyState) {
    init {
        autoLogin()
    }

    override suspend fun handleIntent(intent: EmptyIntent) {}

    private fun autoLogin() {
        viewModelScope.launch {
            val delayJob = async { delay(SPLASH_MIN_DURATION_MS) }
            val refreshJob = async { authRepository.refreshAccessToken() }

            // 최소 스플래시 시간 보장
            delayJob.await()

            // 네트워크 환경이나 여러 딜레이 이슈로 지연되는 경우 최대 4초까지 리프레시 응답 기다리기
            val refreshResult = withTimeoutOrNull(REFRESH_TIMEOUT_MS) { refreshJob.await() }
            if (refreshResult == null) refreshJob.cancel()

            when (refreshResult) {
                is AppResult.Success -> checkOnboardingStatus()
                else -> tryEmitSideEffect(SplashSideEffect.NavigateToLogin) // TODO: 네트워크 연결 불안정은 다이얼로그로 분리
            }
        }
    }

    private suspend fun checkOnboardingStatus() {
        when (val result = onBoardingRepository.fetchOnboardingStatus()) {
            is AppResult.Success ->
                when (result.data) {
                    OnboardingStatus.COMPLETED -> tryEmitSideEffect(SplashSideEffect.NavigateToMain)
                    else -> tryEmitSideEffect(SplashSideEffect.NavigateToOnBoarding(result.data))
                }
            else -> tryEmitSideEffect(SplashSideEffect.NavigateToMain)
        }
    }

    private companion object {
        const val SPLASH_MIN_DURATION_MS = 2_000L
        const val REFRESH_TIMEOUT_MS = 4_000L
    }
}
