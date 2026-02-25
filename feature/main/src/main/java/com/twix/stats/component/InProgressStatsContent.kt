package com.twix.stats.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.calendar.CalendarNavigator
import com.twix.designsystem.components.stats.EmptyStatsGuide
import com.twix.designsystem.components.stats.StatsGoalCard
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.stats.Stats
import com.twix.stats.contract.StatsUiState
import com.twix.stats.preview.StatsUiStatePreviewProvider
import com.twix.ui.extension.noRippleClickable
import java.time.LocalDate

@Composable
fun InProgressStatsContent(
    currentDate: LocalDate,
    stats: Stats,
    modifier: Modifier = Modifier,
    onClickPreviousMonth: () -> Unit,
    onClickNextMonth: () -> Unit,
    onClickStatsCard: (Long) -> Unit,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .background(GrayColor.C050),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            CalendarNavigator(
                currentDate = currentDate,
                onPreviousMonth = onClickPreviousMonth,
                onNextMonth = onClickNextMonth,
                modifier = Modifier.padding(top = 16.dp),
            )
        }

        if (stats.statsGoals.isEmpty()) {
            item {
                EmptyStatsGuide(
                    imageResId = R.drawable.ic_empty_pencil,
                    messageResId = R.string.stats_stamp_not_has_in_progress_goal,
                )
            }
        } else {
            item { Spacer(Modifier.height(12.dp)) }

            items(
                items = stats.statsGoals,
                key = { it.goalId },
            ) {
                StatsGoalCard(
                    statsGoal = it,
                    showStamp = true,
                    modifier =
                        Modifier
                            .noRippleClickable(onClick = { onClickStatsCard(it.goalId) }),
                )
            }
        }
    }
}

@Preview
@Composable
fun InProgressStatsContentPreview(
    @PreviewParameter(StatsUiStatePreviewProvider::class)
    uiState: StatsUiState,
) {
    TwixTheme {
        InProgressStatsContent(
            currentDate = uiState.currentDate,
            stats = uiState.inProgressStats,
            onClickPreviousMonth = {},
            onClickNextMonth = {},
            onClickStatsCard = {},
        )
    }
}
