package com.twix.stats.contract

import androidx.compose.runtime.Immutable
import com.twix.domain.model.stats.Stats
import com.twix.domain.model.stats.StatsGoal
import com.twix.ui.base.State
import java.time.LocalDate

@Immutable
data class StatsUiState(
    val currentDate: LocalDate = LocalDate.now(),
    val inProgressStats: Stats = Stats.EMPTY,
    val endStats: List<StatsGoal> = emptyList(),
) : State
