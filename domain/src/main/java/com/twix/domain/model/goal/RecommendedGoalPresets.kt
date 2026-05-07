package com.twix.domain.model.goal

import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.RecommendedGoalTitleKey
import com.twix.domain.model.enums.RepeatCycle

object RecommendedGoalPresets {
    val items =
        listOf(
            RecommendedGoalPreset(
                id = "workout",
                titleKey = RecommendedGoalTitleKey.WORKOUT,
                icon = GoalIconType.EXERCISE,
                repeatCycle = RepeatCycle.WEEKLY,
                repeatCount = 3,
            ),
            RecommendedGoalPreset(
                id = "vitamin",
                titleKey = RecommendedGoalTitleKey.VITAMIN,
                icon = GoalIconType.HEALTH,
                repeatCycle = RepeatCycle.DAILY,
                repeatCount = 1,
            ),
            RecommendedGoalPreset(
                id = "walk",
                titleKey = RecommendedGoalTitleKey.WALK,
                icon = GoalIconType.DEFAULT,
                repeatCycle = RepeatCycle.MONTHLY,
                repeatCount = 2,
            ),
            RecommendedGoalPreset(
                id = "reading",
                titleKey = RecommendedGoalTitleKey.READING,
                icon = GoalIconType.BOOK,
                repeatCycle = RepeatCycle.MONTHLY,
                repeatCount = 4,
            ),
            RecommendedGoalPreset(
                id = "cleaning",
                titleKey = RecommendedGoalTitleKey.CLEANING,
                icon = GoalIconType.CLEAN,
                repeatCycle = RepeatCycle.WEEKLY,
                repeatCount = 1,
            ),
            RecommendedGoalPreset(
                id = "call_on_way_home",
                titleKey = RecommendedGoalTitleKey.CALL_ON_WAY_HOME,
                icon = GoalIconType.HEART,
                repeatCycle = RepeatCycle.DAILY,
                repeatCount = 1,
            ),
        )

    fun findById(id: String): RecommendedGoalPreset? = items.firstOrNull { it.id == id }
}
