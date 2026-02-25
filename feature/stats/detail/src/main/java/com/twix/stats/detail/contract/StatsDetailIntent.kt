package com.yapp.stats.detail.contract

import com.twix.ui.base.Intent
import java.time.LocalDate

sealed interface StatsDetailIntent : Intent {
    data class SelectDate(
        val date: LocalDate,
    ) : StatsDetailIntent

    data object PreviousMonth : StatsDetailIntent

    data object NextMonth : StatsDetailIntent

    data object GoalEnd : StatsDetailIntent

    data object GoalDelete : StatsDetailIntent
}
