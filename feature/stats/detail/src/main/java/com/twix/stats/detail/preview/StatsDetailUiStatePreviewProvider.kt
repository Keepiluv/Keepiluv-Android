package com.twix.stats.detail.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.twix.designsystem.components.stats.model.StatsCalendarUiModel
import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.RepeatCycle
import com.twix.domain.model.stats.detail.CompletedDate
import com.twix.domain.model.stats.detail.StatsDetail
import com.twix.domain.model.stats.detail.StatsSummary
import com.twix.stats.detail.contract.StatsDetailUiState
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
            status = "진행중",
            monthDate = LocalDate.now(),
            completedDate = completedDates,
            statsSummary =
                StatsSummary(
                    myNickname = "나",
                    partnerNickname = "파트너",
                    totalCount = 10,
                    myCompletedCount = 6,
                    partnerCompletedCount = 4,
                    repeatCycle = RepeatCycle.DAILY,
                    startDate = LocalDate.now().minusMonths(1),
                    endDate = null,
                ),
        )

    private val baseCalendarUiModel =
        StatsCalendarUiModel.create(
            currentDate = LocalDate.now(),
            completedDate = completedDates,
        )

    override val values: Sequence<StatsDetailUiState> =
        sequenceOf(
            StatsDetailUiState(
                goalId = 1,
                detail = baseDetail,
                calendarUiModel = baseCalendarUiModel,
            ),
            StatsDetailUiState(
                goalId = 1,
                detail = baseDetail.copy(status = "종료"),
                calendarUiModel = baseCalendarUiModel,
            ),
        )
}
