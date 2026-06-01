package com.twix.stats.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.StampColor
import com.twix.domain.model.enums.StampType
import com.twix.domain.model.stats.ParticipantStats
import com.twix.domain.model.stats.Stats
import com.twix.domain.model.stats.StatsGoal
import com.twix.result.AppError
import com.twix.stats.contract.StatsUiState
import java.time.LocalDate

class StatsUiStatePreviewProvider : PreviewParameterProvider<StatsUiState> {
    override val values: Sequence<StatsUiState> =
        sequenceOf(
            defaultState(),
            emptyState(),
            loadingState(),
            errorState(),
        )

    private fun defaultState() =
        StatsUiState(
            inProgressStats =
                Stats(
                    selectedDate = LocalDate.now(),
                    statsGoals =
                        listOf(
                            StatsGoal(
                                goalId = 1,
                                goalName = "Goal 1",
                                goalIconType = GoalIconType.DEFAULT,
                                monthlyTargetCount = 10,
                                stamp = StampType.CLOVER,
                                myStats =
                                    ParticipantStats(
                                        nickname = "Me",
                                        completedCount = 5,
                                        stampColors =
                                            listOf(
                                                StampColor.YELLOW400,
                                                StampColor.BLUE400,
                                            ),
                                    ),
                                partnerStats =
                                    ParticipantStats(
                                        nickname = "Partner",
                                        completedCount = 7,
                                        stampColors =
                                            listOf(
                                                StampColor.PINK400,
                                                StampColor.GREEN400,
                                            ),
                                    ),
                            ),
                        ),
                ),
            completedStats = emptyList(),
            isLoadedInProgressStats = true,
            isLoadedCompletedStats = true,
        )

    private fun emptyState() =
        StatsUiState(
            inProgressStats =
                Stats(
                    selectedDate = LocalDate.now(),
                    statsGoals = emptyList(),
                ),
            completedStats = emptyList(),
            isLoadedInProgressStats = true,
            isLoadedCompletedStats = true,
        )

    private fun loadingState() =
        StatsUiState(
            isLoading = true,
        )

    private fun errorState() =
        StatsUiState(
            error = AppError.Network(),
        )
}
