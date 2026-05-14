package com.twix.onboarding.contract

import androidx.compose.runtime.Immutable
import com.twix.onboarding.dday.DdayUiModel
import com.twix.onboarding.invite.InviteCodeUiModel
import com.twix.onboarding.profile.ProfileUiModel
import com.twix.result.AppError
import com.twix.ui.base.LoadableState

@Immutable
data class OnBoardingUiState(
    val profile: ProfileUiModel = ProfileUiModel(),
    val inviteCode: InviteCodeUiModel = InviteCodeUiModel(),
    val dDay: DdayUiModel = DdayUiModel(),
    override val isLoading: Boolean = true,
    override val error: AppError? = null,
    val loadingAction: OnBoardingLoadingAction? = null,
) : LoadableState {
    val hasInviteCodeContent: Boolean
        get() = inviteCode.myInviteCode.isNotBlank()

    val showLoading: Boolean
        get() = isLoading && !hasInviteCodeContent

    val showError: Boolean
        get() = error != null && !hasInviteCodeContent

    val isValidNickName: Boolean
        get() = profile.isValid

    val isConnectingCouple: Boolean
        get() = loadingAction == OnBoardingLoadingAction.CONNECT_COUPLE

    val isSubmittingProfile: Boolean
        get() = loadingAction == OnBoardingLoadingAction.SUBMIT_PROFILE

    val isSubmittingDday: Boolean
        get() = loadingAction == OnBoardingLoadingAction.SUBMIT_DDAY

    val isSubmittingMarketingConsent: Boolean
        get() = loadingAction == OnBoardingLoadingAction.SUBMIT_MARKETING_CONSENT

    override fun copyLoadableState(
        isLoading: Boolean,
        error: AppError?,
    ): LoadableState =
        copy(
            isLoading = isLoading,
            error = error,
        )
}
