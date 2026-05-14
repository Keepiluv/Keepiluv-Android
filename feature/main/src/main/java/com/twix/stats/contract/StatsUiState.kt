package com.twix.stats.contract

import androidx.compose.runtime.Immutable
import com.twix.domain.model.stats.Stats
import com.twix.domain.model.stats.StatsGoal
import com.twix.result.AppError
import com.twix.ui.base.ContentLoadableState
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
) : ContentLoadableState {
    override val hasLoadedContent
        get() = isLoadedInProgressStats || isLoadedCompletedStats

    val showContentLoading get() = showOverlayLoading

    override fun copyState(
        isLoading: Boolean,
        error: AppError?,
    ): ContentLoadableState = copy(isLoading = isLoading, error = error)
}
