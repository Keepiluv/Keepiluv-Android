package com.twix.stats

import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.StampColor
import com.twix.domain.model.enums.StampType
import com.twix.domain.model.stats.ParticipantStats
import com.twix.domain.model.stats.Stats
import com.twix.domain.model.stats.StatsGoal
import com.twix.stats.contract.StatsUiState
import java.time.LocalDate
import kotlin.random.Random

private fun createMonthlyStats(
    totalMonths: Int,
    emptyIndex: Int = -1,
): Stats =
    Stats(
        selectedDate = LocalDate.now(),
        statsGoals =
            List(totalMonths) { index ->
                val isEmpty = index == emptyIndex
                val myCount = if (isEmpty) 0 else Random.nextInt(5, 20)
                val partnerCount = if (isEmpty) 0 else Random.nextInt(5, 20)

                StatsGoal(
                    goalId = index.toLong(),
                    goalName = "아이스크림 먹기",
                    goalIconType = GoalIconType.DEFAULT,
                    monthlyTargetCount = if (isEmpty) 0 else 20,
                    myStats =
                        ParticipantStats(
                            nickname = "찬호",
                            endCount = if (isEmpty) 0 else Random.nextInt(5, 20),
                            stampColors = List(myCount) { StampColor.entries.random() },
                        ),
                    stamp = StampType.entries.random(),
                    partnerStats =
                        ParticipantStats(
                            nickname = "페토",
                            endCount = if (isEmpty) 0 else Random.nextInt(5, 20),
                            stampColors = List(partnerCount) { StampColor.entries.random() },
                        ),
                )
            },
    )

val dummyStatsUiState =
    StatsUiState(
        inProgressStats =
            createMonthlyStats(
                totalMonths = 4,
                emptyIndex = 1,
            ),
        endStats =
            createMonthlyStats(
                totalMonths = 4,
                emptyIndex = 3,
            ),
    )
