package com.twix.stats.contract

import androidx.compose.runtime.Immutable
import com.twix.domain.model.stats.Stats
import com.twix.ui.base.State

@Immutable
data class StatsUiState(
    val inProgressStats: Stats = Stats.EMPTY,
    val endStats: Stats = Stats.EMPTY,
) : State
