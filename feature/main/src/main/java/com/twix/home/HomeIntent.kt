package com.twix.home

import com.twix.domain.model.enums.GoalCheckState
import com.twix.ui.base.Intent
import java.time.LocalDate

sealed interface HomeIntent : Intent {
    data class SelectDate(
        val date: LocalDate,
    ) : HomeIntent

    data object PreviousWeek : HomeIntent

    data object NextWeek : HomeIntent

    data object MoveToToday : HomeIntent

    data class UpdateVisibleDate(
        val date: LocalDate,
    ) : HomeIntent

    data class Verification(
        val goalId: Long,
        val goalCheckState: GoalCheckState,
    ) : HomeIntent

    data class PokeGoal(
        val goalId: Long,
    ) : HomeIntent

    data object Refresh : HomeIntent // 당겨서 리프레시
}
