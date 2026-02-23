package com.twix.domain.model.stats.detail

import com.twix.domain.model.enums.GoalIconType
import java.time.LocalDate

data class StatsDetail(
    val goalId: Long,
    val goalName: String,
    val goalIcon: GoalIconType,
    val status: String,
    val monthDate: LocalDate,
    val completedDate: List<CompletedDate>,
    val statsSummary: StatsSummary,
)
