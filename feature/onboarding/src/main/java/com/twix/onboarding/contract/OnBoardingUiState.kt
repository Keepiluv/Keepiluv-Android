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
    override val isLoading: Boolean = false,
    override val error: AppError? = null,
) : LoadableState {
    val isValidNickName: Boolean
        get() = profile.isValid

    override fun copyLoadableState(
        isLoading: Boolean,
        error: AppError?,
    ): LoadableState = copy(isLoading = isLoading, error = error)
}
