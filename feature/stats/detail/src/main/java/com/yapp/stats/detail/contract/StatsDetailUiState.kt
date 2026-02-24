package com.yapp.stats.detail.contract

import androidx.compose.runtime.Immutable
import com.twix.designsystem.components.stats.model.StatsCalendarUiModel
import com.twix.domain.model.stats.detail.StatsDetail
import com.twix.ui.base.State
import java.time.LocalDate
import java.time.YearMonth

@Immutable
data class StatsDetailUiState(
    val goalId: Long = -1,
    val detail: StatsDetail = StatsDetail.EMPTY,
    val isInProgressStatsDetail: Boolean = true,
    val calendarUiModel: StatsCalendarUiModel = StatsCalendarUiModel(),
) : State {
    val hasNext: Boolean
        get() {
            val limitYm = YearMonth.from(detail.statsSummary.endDate ?: LocalDate.now())
            val nextYm = YearMonth.from(detail.monthDate).plusMonths(1)

            return nextYm <= limitYm
        }

    val hasPrevious: Boolean
        get() {
            val limitYm = YearMonth.from(detail.statsSummary.startDate)
            val previousYm = YearMonth.from(detail.monthDate).minusMonths(1)
            return previousYm >= limitYm
        }
}
