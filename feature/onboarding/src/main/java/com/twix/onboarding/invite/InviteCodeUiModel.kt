package com.twix.onboarding.invite

import androidx.compose.runtime.Immutable

@Immutable
data class InviteCodeUiModel(
    val myInviteCode: String = "",
    val partnerInviteCode: String = "",
    val isValid: Boolean = false,
)
