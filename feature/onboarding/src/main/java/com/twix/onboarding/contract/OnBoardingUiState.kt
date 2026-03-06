package com.twix.onboarding.contract

import androidx.compose.runtime.Immutable
import com.twix.onboarding.dday.DdayUiModel
import com.twix.onboarding.invite.InviteCodeUiModel
import com.twix.onboarding.profile.ProfileUiModel
import com.twix.ui.base.State
import java.time.LocalDate

@Immutable
data class OnBoardingUiState(
    val profile: ProfileUiModel = ProfileUiModel(),
    val inviteCode: InviteCodeUiModel = InviteCodeUiModel(),
    val dDay: DdayUiModel = DdayUiModel(),
) : State {
    val isValidNickName: Boolean
        get() = profile.isValid

    fun updateNickName(value: String) = copy(profile = profile.updateNickname(value))

    fun updateMyInviteCode(value: String) = copy(inviteCode = inviteCode.updateMyInviteCode(value))

    fun updatePartnerInviteCode(value: String) = copy(inviteCode = inviteCode.updatePartnerInviteCode(value))

    fun updateDday(value: LocalDate) = copy(dDay = dDay.updateAnniversaryDate(value))
}
