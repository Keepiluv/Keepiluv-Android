package com.twix.stats.detail.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.twix.designsystem.components.stats.model.StatsCalendarUiModel
import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.RepeatCycle
import com.twix.domain.model.stats.detail.CompletedDate
import com.twix.domain.model.stats.detail.StatsDetail
import com.twix.domain.model.stats.detail.StatsSummary
import com.twix.result.AppError
import com.twix.stats.detail.contract.StatsDetailUiState
import java.io.IOException
import java.time.LocalDate

class StatsDetailUiStatePreviewProvider : PreviewParameterProvider<StatsDetailUiState> {
    private val completedDates =
        listOf(
            CompletedDate(
                date = LocalDate.now().minusDays(1),
                myImageUrl = "https://picsum.photos/200",
                partnerImageUrl = "https://picsum.photos/201",
            ),
            CompletedDate(
                date = LocalDate.now().minusDays(3),
                myImageUrl = "https://picsum.photos/202",
                partnerImageUrl = null,
            ),
            CompletedDate(
                date = LocalDate.now().minusDays(5),
                myImageUrl = null,
                partnerImageUrl = "https://picsum.photos/203",
            ),
        )

    private val baseDetail =
        StatsDetail(
            goalId = 1,
            goalName = "아이스크림 먹기",
            goalIcon = GoalIconType.DEFAULT,
            isCompleted = false,
            currentDate = LocalDate.now(),
            completedDate = completedDates,
        )

    private val summary =
        StatsSummary(
            myNickname = "나",
            partnerNickname = "파트너",
            totalCount = 10,
            myCompletedCount = 6,
            partnerCompletedCount = 4,
            repeatCycle = RepeatCycle.DAILY,
            startDate = LocalDate.now().minusMonths(1),
            endDate = null,
        )

    private val baseCalendarUiModel =
        StatsCalendarUiModel.create(
            currentDate = LocalDate.now(),
            completedDate = completedDates,
        )

    override val values: Sequence<StatsDetailUiState> =
        sequenceOf(
            StatsDetailUiState(
                detail = baseDetail,
                summary = summary,
                calendarUiModel = baseCalendarUiModel,
                isLoading = false,
            ),
            StatsDetailUiState(
                detail = baseDetail.copy(isCompleted = true),
                summary = summary,
                calendarUiModel = baseCalendarUiModel,
                isLoading = false,
            ),
            StatsDetailUiState(
                detail = baseDetail,
                summary = summary,
                calendarUiModel = baseCalendarUiModel,
                isLoading = true,
            ),
            StatsDetailUiState(
                isLoading = false,
                error =
                    AppError.Network(
                        IOException(),
                    ),
            ),
        )
}
