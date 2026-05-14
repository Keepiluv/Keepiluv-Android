package com.twix.stats.detail.contract

import androidx.compose.runtime.Immutable
import com.twix.designsystem.components.stats.model.StatsCalendarUiModel
import com.twix.domain.model.stats.detail.StatsDetail
import com.twix.domain.model.stats.detail.StatsSummary
import com.twix.result.AppError
import com.twix.ui.base.ContentLoadableState
import java.time.LocalDate
import java.time.YearMonth

@Immutable
data class StatsDetailUiState(
    val detail: StatsDetail = StatsDetail.EMPTY,
    val summary: StatsSummary = StatsSummary.EMPTY,
    val calendarUiModel: StatsCalendarUiModel = StatsCalendarUiModel(),
    override val hasLoadedContent: Boolean = false,
    override val isLoading: Boolean = true,
    override val error: AppError? = null,
) : ContentLoadableState {
    val hasNext: Boolean
        get() {
            val limitYm = YearMonth.from(summary.endDate ?: LocalDate.now())
            val nextYm = YearMonth.from(detail.currentDate).plusMonths(1)

            return nextYm <= limitYm
        }

    val hasPrevious: Boolean
        get() {
            val limitYm = YearMonth.from(summary.startDate)
            val previousYm = YearMonth.from(detail.currentDate).minusMonths(1)
            return previousYm >= limitYm
        }

    override fun copyState(
        isLoading: Boolean,
        error: AppError?,
    ): ContentLoadableState =
        copy(
            isLoading = isLoading,
            error = error,
        )
}
