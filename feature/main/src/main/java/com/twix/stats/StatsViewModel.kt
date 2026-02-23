package com.twix.stats

import com.twix.stats.contract.StatsIntent
import com.twix.stats.contract.StatsSideEffect
import com.twix.stats.contract.StatsUiState
import com.twix.ui.base.BaseViewModel

class StatsViewModel : BaseViewModel<StatsUiState, StatsIntent, StatsSideEffect>(dummyStatsUiState) {
    override suspend fun handleIntent(intent: StatsIntent) {
        when (intent) {
            is StatsIntent.OnClickPreviousMonth -> fetchPreviousMonthStats()
            is StatsIntent.OnClickNextMonth -> fetchNextMonthStats()
        }
    }

    private fun fetchPreviousMonthStats() {
        val previousMonth = currentState.inProgressStats.previousMonth()
        // TODO : API 호출
    }

    private fun fetchNextMonthStats() {
        val nextMonth = currentState.inProgressStats.nextMonth()
        // TODO : API 호출
    }
}
