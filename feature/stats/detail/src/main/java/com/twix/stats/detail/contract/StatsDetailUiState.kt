package com.twix.stats.detail.contract

import androidx.compose.runtime.Immutable
import com.twix.designsystem.components.stats.model.StatsCalendarUiModel
import com.twix.domain.model.stats.detail.StatsDetail
import com.twix.domain.model.stats.detail.StatsSummary
import com.twix.ui.base.State
import java.time.LocalDate
import java.time.YearMonth

@Immutable
data class StatsDetailUiState(
    val goalId: Long = -1,
    val detail: StatsDetail = StatsDetail.EMPTY,
    val summary: StatsSummary = StatsSummary.EMPTY,
    val calendarUiModel: StatsCalendarUiModel = StatsCalendarUiModel(),
) : State {
    val hasNext: Boolean
        get() {
            val limitYm = YearMonth.from(summary.endDate ?: LocalDate.now())
            val nextYm = YearMonth.from(detail.yearMonth).plusMonths(1)

            return nextYm <= limitYm
        }

    val hasPrevious: Boolean
        get() {
            val limitYm = YearMonth.from(summary.startDate)
            val previousYm = YearMonth.from(detail.yearMonth).minusMonths(1)
            return previousYm >= limitYm
        }
}
