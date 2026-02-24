package com.twix.domain.model.stats

import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.StampType

data class StatsGoal(
    val goalId: Long,
    val goalName: String,
    val goalIconType: GoalIconType,
    val monthlyTargetCount: Int,
    val stamp: StampType,
    val myStats: ParticipantStats,
    val partnerStats: ParticipantStats,
)
