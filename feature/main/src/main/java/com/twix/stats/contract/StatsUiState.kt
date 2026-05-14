package com.twix.stats.contract

import androidx.compose.runtime.Immutable
import com.twix.domain.model.stats.Stats
import com.twix.domain.model.stats.StatsGoal
import com.twix.result.AppError
import com.twix.ui.base.LoadableState
import java.time.LocalDate

@Immutable
data class StatsUiState(
    val currentDate: LocalDate = LocalDate.now(),
    val inProgressStats: Stats = Stats.EMPTY,
    val completedStats: List<StatsGoal> = emptyList(),
    val isLoadedInProgressStats: Boolean = false,
    val isLoadedCompletedStats: Boolean = false,
    override val isLoading: Boolean = false,
    override val error: AppError? = null,
) : LoadableState {
    private val hasLoadedAnyStats
        get() = isLoadedInProgressStats || isLoadedCompletedStats

    val showLoading get() = isLoading && !hasLoadedAnyStats

    val showError get() = error != null && !hasLoadedAnyStats

    val showContentLoading get() = isLoading && hasLoadedAnyStats

    override fun copyLoadableState(
        isLoading: Boolean,
        error: AppError?,
    ): LoadableState = copy(isLoading = isLoading, error = error)
}
