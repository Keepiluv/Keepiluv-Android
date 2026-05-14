package com.twix.onboarding.contract

import androidx.compose.runtime.Immutable
import com.twix.onboarding.dday.DdayUiModel
import com.twix.onboarding.invite.InviteCodeUiModel
import com.twix.onboarding.profile.ProfileUiModel
import com.twix.result.AppError
import com.twix.ui.base.ContentLoadableState

@Immutable
data class OnBoardingUiState(
    val profile: ProfileUiModel = ProfileUiModel(),
    val inviteCode: InviteCodeUiModel = InviteCodeUiModel(),
    val dDay: DdayUiModel = DdayUiModel(),
    val loadingAction: OnBoardingLoadingAction? = null,
    override val hasLoadedContent: Boolean = false,
    override val isLoading: Boolean = true,
    override val error: AppError? = null,
) : ContentLoadableState {
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

    override fun copyState(
        isLoading: Boolean,
        error: AppError?,
    ): ContentLoadableState =
        copy(
            isLoading = isLoading,
            error = error,
        )
}
