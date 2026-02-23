package com.twix.login

import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val onBoardingRepository: OnBoardingRepository,
) : BaseViewModel<LoginUiState, LoginIntent, LoginSideEffect>(LoginUiState()) {
    override suspend fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Login -> login(intent.result)
        }
    }

    private fun login(result: LoginResult) {
        viewModelScope.launch {
            when (result) {
                is LoginResult.Success -> {
                    authRepository.login(result.idToken, result.type)
                    checkOnboardingStatus()
                }

                is LoginResult.Failure -> {
                    LoginSideEffect.ShowToast(
                        message = R.string.login_fail_message,
                        type = ToastType.ERROR,
                    )
                }

                LoginResult.Cancel -> Unit
            }
        }
    }

    private fun checkOnboardingStatus() {
        launchResult(
            block = { onBoardingRepository.fetchOnboardingStatus() },
            onSuccess = { onboardingStatus ->
                viewModelScope.launch {
                    val sideEffect =
                        when (onboardingStatus) {
                            OnboardingStatus.COUPLE_CONNECTION,
                            OnboardingStatus.PROFILE_SETUP,
                            OnboardingStatus.ANNIVERSARY_SETUP,
                            -> LoginSideEffect.NavigateToOnBoarding(onboardingStatus)

                            OnboardingStatus.COMPLETED -> LoginSideEffect.NavigateToHome
                        }

                    emitSideEffect(sideEffect)
                }
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
}
