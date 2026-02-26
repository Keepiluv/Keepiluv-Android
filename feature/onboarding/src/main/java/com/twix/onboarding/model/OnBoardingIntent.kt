package com.twix.onboarding.model

import com.twix.ui.base.Intent
import java.time.LocalDate

sealed interface OnBoardingIntent : Intent {
    data class SubmitMarketingConsent(
        val isPushEnabled: Boolean,
        val isMarketingEnabled: Boolean,
        val isNightMarketingEnabled: Boolean,
    ) : OnBoardingIntent

    data class WriteNickName(
        val value: String,
    ) : OnBoardingIntent

    data object SubmitNickName : OnBoardingIntent

    data class WriteInviteCode(
        val value: String,
    ) : OnBoardingIntent

    data object CopyInviteCode : OnBoardingIntent

    data object ConnectCouple : OnBoardingIntent

    data class SelectDate(
        val value: LocalDate,
    ) : OnBoardingIntent

    data object SubmitDday : OnBoardingIntent
}
