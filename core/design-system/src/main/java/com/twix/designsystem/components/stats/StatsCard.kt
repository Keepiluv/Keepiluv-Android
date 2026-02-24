package com.twix.designsystem.components.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.components.goal.GoalCardFrame
import com.twix.designsystem.components.stamp.StampGrid
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.StampColor
import com.twix.domain.model.enums.StampType
import com.twix.domain.model.stats.ParticipantStats
import com.twix.domain.model.stats.StatsGoal

@Composable
fun StatsCard(
    statsGoal: StatsGoal,
    showStamp: Boolean,
    right: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    GoalCardFrame(
        goalName = statsGoal.goalName,
        goalIcon = statsGoal.goalIconType,
        right = { right() },
        content = { StatsCardContent(statsGoal, showStamp) },
        modifier = modifier,
    )
}

@Composable
private fun StatsCardContent(
    statsGoal: StatsGoal,
    showStamp: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalDivider(thickness = 1.dp, color = GrayColor.C500)
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            StampCell(
                stats = statsGoal.myStats,
                content = {
                    if (showStamp) {
                        Spacer(modifier = Modifier.height(12.dp))

                        StampGrid(
                            stamp = statsGoal.stamp,
                            monthlyTargetCount = statsGoal.monthlyTargetCount,
                            stampColors = statsGoal.myStats.stampColors,
                        )
                    }
                },
                modifier =
                    Modifier
                        .padding(16.dp)
                        .weight(1f),
            )

            VerticalDivider(thickness = 1.dp, color = GrayColor.C500)

            StampCell(
                stats = statsGoal.partnerStats,
                content = {
                    if (showStamp) {
                        Spacer(modifier = Modifier.height(12.dp))

                        StampGrid(
                            stamp = statsGoal.stamp,
                            monthlyTargetCount = statsGoal.monthlyTargetCount,
                            stampColors = statsGoal.partnerStats.stampColors,
                        )
                    }
                },
                modifier =
                    Modifier
                        .padding(16.dp)
                        .weight(1f),
            )
        }
    }
}

@Preview
@Composable
fun StatsCardPreview() {
    TwixTheme {
        StatsCard(
            statsGoal =
                StatsGoal(
                    goalId = 1,
                    goalIconType = GoalIconType.DEFAULT,
                    goalName = "운동",
                    stamp = StampType.HEART,
                    monthlyTargetCount = 10,
                    myStats =
                        ParticipantStats(
                            nickname = "페토",
                            completedCount = 10,
                            stampColors = listOf(StampColor.PINK400),
                        ),
                    partnerStats =
                        ParticipantStats(
                            nickname = "찬호",
                            completedCount = 5,
                            stampColors = listOf(StampColor.PINK400),
                        ),
                ),
            showStamp = true,
            right = {},
        )
    }
}
