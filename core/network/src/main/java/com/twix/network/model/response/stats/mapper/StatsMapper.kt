package com.twix.network.model.response.stats.mapper

import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.StampColor
import com.twix.domain.model.enums.StampType
import com.twix.domain.model.stats.ParticipantStats
import com.twix.domain.model.stats.Stats
import com.twix.domain.model.stats.StatsGoal
import com.twix.network.model.response.stats.model.ParticipantStatsResponse
import com.twix.network.model.response.stats.model.StatsGoalResponse
import com.twix.network.model.response.stats.model.StatsResponse
import java.time.LocalDate

fun StatsResponse.toDomain(): Stats =
    Stats(
        selectedDate = LocalDate.parse(selectedDate),
        statsGoals = statsGoals.map { it.toDomain() },
    )

private fun StatsGoalResponse.toDomain(): StatsGoal =
    StatsGoal(
        goalId = goalId,
        goalName = goalName,
        goalIconType = GoalIconType.fromApi(goalIconType),
        monthlyTargetCount = monthlyTargetCount,
        stamp = stamp.toStampType(),
        myStats = myStats.toDomain(),
        partnerStats = partnerStats.toDomain(),
    )

private fun ParticipantStatsResponse.toDomain(): ParticipantStats =
    ParticipantStats(
        nickname = nickname,
        completedCount = endCount,
        stampColors = stampColors.map { it.toStampColor() },
    )

private fun String.toStampType(): StampType = runCatching { StampType.valueOf(this) }.getOrElse { StampType.CLOVER }

private fun String.toStampColor(): StampColor = runCatching { StampColor.valueOf(this) }.getOrElse { StampColor.GREEN400 }
