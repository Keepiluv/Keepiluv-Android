package com.twix.login

import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.login.LoginResult
import com.twix.domain.model.OnboardingStatus
import com.twix.domain.repository.AuthRepository
import com.twix.domain.repository.OnBoardingRepository
import com.twix.login.contract.LoginIntent
import com.twix.login.contract.LoginSideEffect
import com.twix.login.contract.LoginUiState
import com.twix.ui.base.BaseViewModel

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val onBoardingRepository: OnBoardingRepository,
) : BaseViewModel<LoginUiState, LoginIntent, LoginSideEffect>(LoginUiState()) {
    override suspend fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Login -> login(intent.result)
        }
    }

    private suspend fun login(result: LoginResult) {
        if (currentState.isLoading) return

        when (result) {
            is LoginResult.Success -> authenticate(result)
            is LoginResult.Failure -> showToast(R.string.login_fail_message)
            LoginResult.Cancel -> Unit
        }
    }

    private fun authenticate(result: LoginResult.Success) {
        launchResult(
            block = { authRepository.login(result.idToken, result.type) },
            onSuccess = { checkOnboardingStatus() },
            onError = { showToast(R.string.login_fail_message) },
        )
    }

    private fun checkOnboardingStatus() {
        launchResult(
            block = { onBoardingRepository.fetchOnboardingStatus() },
            onSuccess = { onboardingStatus ->
                tryEmitSideEffect(onboardingStatus.toSideEffect())
            },
            onError = {
                emitSideEffect(
                    LoginSideEffect.ShowToast(
                        message = R.string.fetch_onboarding_status_fail_message,
                        type = ToastType.ERROR,
                    ),
                )
            },
        )
    }

    private fun OnboardingStatus.toSideEffect(): LoginSideEffect {
        if (this == OnboardingStatus.COMPLETED) {
            return LoginSideEffect.NavigateToHome
        }

        return LoginSideEffect.NavigateToOnBoarding(this)
    }

    private suspend fun showToast(message: Int) {
        emitSideEffect(
            LoginSideEffect.ShowToast(
                message = message,
                type = ToastType.ERROR,
            ),
        )
    }
}
