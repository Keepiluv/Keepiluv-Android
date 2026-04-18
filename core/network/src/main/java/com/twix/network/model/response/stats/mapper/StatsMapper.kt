package com.twix.network.model.response.stats.mapper

import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.RepeatCycle
import com.twix.domain.model.enums.StampColor
import com.twix.domain.model.enums.StampType
import com.twix.domain.model.stats.ParticipantStats
import com.twix.domain.model.stats.Stats
import com.twix.domain.model.stats.StatsGoal
import com.twix.domain.model.stats.detail.CompletedDate
import com.twix.domain.model.stats.detail.StatsDetail
import com.twix.domain.model.stats.detail.StatsSummary
import com.twix.network.model.response.stats.model.CompletedDateResponse
import com.twix.network.model.response.stats.model.ParticipantStatsResponse
import com.twix.network.model.response.stats.model.StatsCalendarResponse
import com.twix.network.model.response.stats.model.StatsGoalResponse
import com.twix.network.model.response.stats.model.StatsResponse
import com.twix.network.model.response.stats.model.StatsSummaryResponse
import java.time.LocalDate
import java.time.YearMonth

fun StatsResponse.toDomain(): Stats =
    Stats(
        selectedDate = LocalDate.parse(selectedDate),
        statsGoals = statsGoals.map { it.toDomain() },
    )

fun StatsCalendarResponse.toDomain(fallbackMonth: YearMonth): StatsDetail =
    StatsDetail(
        goalId = goalId,
        goalName = goalName,
        goalIcon = GoalIconType.fromApi(goalIcon),
        isCompleted = isCompleted,
        currentDate = runCatching { YearMonth.parse(yearMonth).atDay(1) }.getOrElse { fallbackMonth.atDay(1) },
        completedDate = completedDates.map { it.toDomain() },
    )

fun StatsSummaryResponse.toDomain(): StatsSummary =
    StatsSummary(
        myNickname = myNickname,
        partnerNickname = partnerNickname,
        totalCount = totalCount,
        myCompletedCount = myCompletedCount,
        partnerCompletedCount = partnerCompletedCount,
        repeatCycle = RepeatCycle.fromApi(repeatCycle),
        startDate = LocalDate.parse(startDate),
        endDate = endDate?.let(LocalDate::parse),
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

private fun CompletedDateResponse.toDomain(): CompletedDate =
    CompletedDate(
        date = LocalDate.parse(date),
        myImageUrl = myImageUrl,
        partnerImageUrl = partnerImageUrl,
    )

private fun String.toStampType(): StampType = runCatching { StampType.valueOf(this) }.getOrElse { StampType.CLOVER }

private fun String.toStampColor(): StampColor = runCatching { StampColor.valueOf(this) }.getOrElse { StampColor.GREEN400 }
