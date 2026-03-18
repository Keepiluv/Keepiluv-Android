package com.twix.onboarding.contract

import com.twix.designsystem.components.toast.model.ToastType
import com.twix.ui.base.SideEffect

sealed interface OnBoardingSideEffect : SideEffect {
    sealed interface ProfileSetting : OnBoardingSideEffect {
        data object NavigateToNext : ProfileSetting

        data object NavigateToHome : ProfileSetting
    }

    sealed interface InviteCode : OnBoardingSideEffect {
        data object NavigateToNext : InviteCode

        data class CopyInviteCode(
            val inviteCode: String,
        ) : InviteCode
    }

    sealed interface DdaySetting : OnBoardingSideEffect {
        data object NavigateToHome : DdaySetting
    }

    data class ShowToast(
        val message: Int,
        val type: ToastType,
    ) : OnBoardingSideEffect
}
