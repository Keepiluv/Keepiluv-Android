package com.twix.stats.contract

import com.twix.ui.base.Intent

sealed interface StatsIntent : Intent {
    data object OnClickPreviousMonth : StatsIntent

    data object OnClickNextMonth : StatsIntent
}
