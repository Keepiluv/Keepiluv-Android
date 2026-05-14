package com.twix.stats.contract

import com.twix.ui.base.Intent

sealed interface StatsIntent : Intent {
    data object Retry : StatsIntent

    data object PreviousMonth : StatsIntent

    data object NextMonth : StatsIntent
}
