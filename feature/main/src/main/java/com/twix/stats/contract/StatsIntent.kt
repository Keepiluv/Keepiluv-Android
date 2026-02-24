package com.twix.stats.contract

import com.twix.ui.base.Intent

sealed interface StatsIntent : Intent {
    data object PreviousMonth : StatsIntent

    data object NextMonth : StatsIntent
}
