package com.yapp.stats.detail.contract

import com.twix.ui.base.Intent

sealed interface StatsDetailIntent : Intent {
    data object PreviousMonth : StatsDetailIntent

    data object NextMonth : StatsDetailIntent

    data object GoalEnd : StatsDetailIntent

    data object GoalDelete : StatsDetailIntent
}
