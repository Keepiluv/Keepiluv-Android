package com.twix.notification.contract

import com.twix.ui.base.SideEffect
import java.time.LocalDate

interface NotificationSideEffect : SideEffect {
    data object NavigateToHome : NotificationSideEffect

    data class NavigateToPartnerPhotolog(
        val goalId: Long,
        val date: LocalDate,
    ) : NotificationSideEffect

    data class NavigateToMyPhotolog(
        val goalId: Long,
        val date: LocalDate,
    ) : NotificationSideEffect

    data object NavigateToStatisticsEndedGoals : NotificationSideEffect
}
