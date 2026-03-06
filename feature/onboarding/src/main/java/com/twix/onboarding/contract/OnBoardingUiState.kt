package com.twix.onboarding.contract

import androidx.compose.runtime.Immutable
import com.twix.onboarding.dday.DdayUiModel
import com.twix.onboarding.invite.InviteCodeUiModel
import com.twix.onboarding.profile.ProfileUiModel
import com.twix.ui.base.State

@Immutable
data class OnBoardingUiState(
    val profile: ProfileUiModel = ProfileUiModel(),
    val inviteCode: InviteCodeUiModel = InviteCodeUiModel(),
    val dDay: DdayUiModel = DdayUiModel(),
) : State {
    val isValidNickName: Boolean
        get() = profile.isValid
}
