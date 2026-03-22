package com.twix.splash.contract

import com.twix.domain.model.OnboardingStatus
import com.twix.ui.base.SideEffect

sealed interface SplashSideEffect : SideEffect {
    data object NavigateToMain : SplashSideEffect

    data object NavigateToLogin : SplashSideEffect

    data class NavigateToOnBoarding(
        val status: OnboardingStatus,
    ) : SplashSideEffect
}
