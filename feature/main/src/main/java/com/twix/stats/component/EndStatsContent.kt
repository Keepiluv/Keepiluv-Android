package com.twix.stats.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.stats.EmptyStatsGuide
import com.twix.designsystem.components.stats.StatsGoalCard
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.stats.StatsGoal
import com.twix.ui.extension.noRippleClickable

@Composable
fun EndStatsContent(
    statsGoals: List<StatsGoal>,
    onClickStatsCard: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .background(GrayColor.C050),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (statsGoals.isEmpty()) {
            item {
                EmptyStatsGuide(
                    imageResId = R.drawable.ic_empty_trash,
                    messageResId = R.string.stats_stamp_not_has_complete_goal,
                )
            }
        } else {
            item { Spacer(Modifier.height(20.dp)) }

            items(
                items = statsGoals,
                key = { it.goalId },
            ) {
                StatsGoalCard(
                    statsGoal = it,
                    showStamp = false,
                    modifier = Modifier.noRippleClickable(onClick = { onClickStatsCard(it.goalId) }),
                )
            }
        }
    }
}
