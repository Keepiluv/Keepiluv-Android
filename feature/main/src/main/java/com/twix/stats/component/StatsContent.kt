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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.calendar.CalendarNavigator
import com.twix.designsystem.components.stats.EmptyStatsGuide
import com.twix.designsystem.components.stats.StatsCard
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.stats.Stats
import com.twix.domain.model.stats.StatsGoal
import com.twix.stats.dummyStatsUiState
import com.twix.stats.model.StatsTabDestination

@Composable
fun StatsContent(
    stats: Stats,
    currentTab: StatsTabDestination,
    modifier: Modifier = Modifier,
    onClickPreviousMonth: () -> Unit = {},
    onClickNextMonth: () -> Unit = {},
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .background(GrayColor.C050),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (currentTab == StatsTabDestination.IN_PROGRESS) {
            item {
                CalendarNavigator(
                    currentDate = stats.selectedDate,
                    onPreviousMonth = onClickPreviousMonth,
                    onNextMonth = onClickNextMonth,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
        }

        if (stats.statsGoals.isEmpty()) {
            item {
                EmptyStatsGuide(
                    text =
                        when (currentTab) {
                            StatsTabDestination.IN_PROGRESS -> stringResource(R.string.stats_stamp_not_has_in_progress_goal)
                            StatsTabDestination.END -> stringResource(R.string.stats_stamp_not_has_complete_goal)
                        },
                )
            }
        } else {
            item { Spacer(Modifier.height(12.dp)) }

            items(
                items = stats.statsGoals,
                key = { it.goalId },
            ) {
                StatsGoalCard(it, currentTab)
            }
        }
    }
}

@Composable
private fun StatsGoalCard(
    statsGoal: StatsGoal,
    currentTab: StatsTabDestination,
) {
    StatsCard(
        statsGoal = statsGoal,
        showStamp = currentTab == StatsTabDestination.IN_PROGRESS,
        right = {
            when (currentTab) {
                StatsTabDestination.IN_PROGRESS -> {
                    AppText(
                        text =
                            stringResource(R.string.stats_stamp_total_count).format(
                                statsGoal.monthlyTargetCount,
                            ),
                        style = AppTextStyle.B1,
                        color = GrayColor.C500,
                    )
                }

                StatsTabDestination.END -> {
                    AppText(
                        text =
                            stringResource(R.string.stats_stamp_total_end_count).format(
                                statsGoal.monthlyTargetCount,
                            ),
                        style = AppTextStyle.B1,
                        color = GrayColor.C500,
                    )
                }
            }
        },
        modifier =
            Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
    )
}

@Preview
@Composable
fun StatsContentPreview() {
    TwixTheme {
        StatsContent(
            stats = dummyStatsUiState.inProgressStats,
            currentTab = StatsTabDestination.IN_PROGRESS,
            onClickPreviousMonth = {},
            onClickNextMonth = {},
        )
    }
}
