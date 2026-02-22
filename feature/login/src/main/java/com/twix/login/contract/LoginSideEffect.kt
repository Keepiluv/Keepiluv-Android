package com.twix.login.contract

import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.OnboardingStatus
import com.twix.ui.base.SideEffect

sealed interface LoginSideEffect : SideEffect {
    data object NavigateToHome : LoginSideEffect

    data class NavigateToOnBoarding(
        val status: OnboardingStatus,
    ) : LoginSideEffect

    data class ShowToast(
        val message: Int,
        val type: ToastType,
    ) : LoginSideEffect
}
