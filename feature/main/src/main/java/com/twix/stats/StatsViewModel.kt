package com.twix.stats

import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.repository.StatsRepository
import com.twix.stats.contract.StatsIntent
import com.twix.stats.contract.StatsSideEffect
import com.twix.stats.contract.StatsUiState
import com.twix.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

class StatsViewModel(
    private val statsRepository: StatsRepository,
) : BaseViewModel<StatsUiState, StatsIntent, StatsSideEffect>(StatsUiState()) {
    init {
        fetchInProgressStats(LocalDate.now())
        fetchEndStats()
    }

    override suspend fun handleIntent(intent: StatsIntent) {
        when (intent) {
            is StatsIntent.PreviousMonth -> fetchPreviousMonthStats()
            is StatsIntent.NextMonth -> fetchNextMonthStats()
        }
    }

    private fun fetchInProgressStats(date: LocalDate) {
        launchResult(
            block = { statsRepository.fetchInProgressStats(date) },
            onSuccess = { reduce { copy(inProgressStats = it) } },
            onError = { showToast(R.string.toast_fetch_stats_failed, ToastType.ERROR) },
        )
    }

    private fun fetchPreviousMonthStats() {
        val previousMonth = currentState.currentDate.minusMonths(1)
        reduce { copy(currentDate = previousMonth) }
        fetchInProgressStats(previousMonth)
    }

    private fun fetchNextMonthStats() {
        val nextMonth = currentState.currentDate.plusMonths(1)
        reduce { copy(currentDate = nextMonth) }
        fetchInProgressStats(nextMonth)
    }

    private fun fetchEndStats() {
        launchResult(
            block = { statsRepository.fetchEndStats() },
            onSuccess = { reduce { copy(endStats = it) } },
            onError = { showToast(R.string.toast_fetch_stats_failed, ToastType.ERROR) },
        )
    }

    private fun showToast(
        message: Int,
        type: ToastType,
    ) {
        viewModelScope.launch {
            emitSideEffect(StatsSideEffect.ShowToast(message, type))
        }
    }
}
