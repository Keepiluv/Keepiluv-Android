package com.twix.designsystem.components.stats

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.StampColor
import com.twix.domain.model.enums.StampType
import com.twix.domain.model.stats.ParticipantStats
import com.twix.domain.model.stats.StatsGoal

@Composable
fun StatsGoalCard(
    statsGoal: StatsGoal,
    showStamp: Boolean,
    modifier: Modifier = Modifier,
) {
    StatsCard(
        statsGoal = statsGoal,
        showStamp = showStamp,
        right = {
            AppText(
                text =
                    if (showStamp) {
                        stringResource(R.string.stats_stamp_total_count, statsGoal.monthlyTargetCount)
                    } else {
                        stringResource(R.string.stats_stamp_total_end_count, statsGoal.monthlyTargetCount)
                    },
                style = AppTextStyle.B1,
                color = GrayColor.C500,
            )
        },
        modifier =
            modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
    )
}

@Preview(name = "StatsGoalCard")
@Composable
fun StatsGoalCardPreview() {
    TwixTheme {
        StatsGoalCard(
            statsGoal =
                StatsGoal(
                    goalId = 1,
                    goalName = "Goal Name",
                    goalIconType = GoalIconType.DEFAULT,
                    monthlyTargetCount = 10,
                    stamp = StampType.CLOVER,
                    myStats =
                        ParticipantStats(
                            nickname = "Me",
                            endCount = 5,
                            stampColors = listOf(StampColor.BLUE400, StampColor.PINK400),
                        ),
                    partnerStats =
                        ParticipantStats(
                            nickname = "Partner",
                            endCount = 3,
                            stampColors = listOf(StampColor.GREEN400, StampColor.YELLOW400),
                        ),
                ),
            showStamp = false,
        )
    }
}
