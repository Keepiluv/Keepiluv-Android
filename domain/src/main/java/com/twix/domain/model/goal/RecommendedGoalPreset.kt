package com.twix.domain.model.goal

import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.RecommendedGoalTitleKey
import com.twix.domain.model.enums.RepeatCycle

data class RecommendedGoalPreset(
    val id: String,
    val titleKey: RecommendedGoalTitleKey,
    val icon: GoalIconType,
    val repeatCycle: RepeatCycle,
    val repeatCount: Int,
)
