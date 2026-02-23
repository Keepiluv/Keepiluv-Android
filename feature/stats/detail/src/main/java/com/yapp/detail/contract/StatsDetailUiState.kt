package com.yapp.detail.contract

import androidx.compose.runtime.Immutable
import com.twix.designsystem.components.stats.model.StatsCalendarUiModel
import com.twix.domain.model.stats.detail.StatsDetail
import com.twix.ui.base.State
import java.time.LocalDate

@Immutable
data class StatsDetailUiState(
    val goalId: Long = -1,
    val selectedDate: LocalDate? = null,
    val detail: StatsDetail = StatsDetail.EMPTY,
    val calendarUiModel: StatsCalendarUiModel = StatsCalendarUiModel(),
) : State {
    val isInProgressStatsDetail get() = selectedDate != null
}
