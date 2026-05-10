package com.twix.domain.model.goal

import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.RecommendedGoalTitleKey
import com.twix.domain.model.enums.RepeatCycle

object RecommendedGoalPresets {
    private const val WORKOUT_ID = "workout"
    private const val VITAMIN_ID = "vitamin"
    private const val WALK_ID = "walk"
    private const val READING_ID = "reading"
    private const val CLEANING_ID = "cleaning"
    private const val CALL_ON_WAY_HOME_ID = "call_on_way_home"

    val items =
        listOf(
            RecommendedGoalPreset(
                id = WORKOUT_ID,
                titleKey = RecommendedGoalTitleKey.WORKOUT,
                icon = GoalIconType.EXERCISE,
                repeatCycle = RepeatCycle.WEEKLY,
                repeatCount = 3,
            ),
            RecommendedGoalPreset(
                id = VITAMIN_ID,
                titleKey = RecommendedGoalTitleKey.VITAMIN,
                icon = GoalIconType.HEALTH,
                repeatCycle = RepeatCycle.DAILY,
                repeatCount = 1,
            ),
            RecommendedGoalPreset(
                id = WALK_ID,
                titleKey = RecommendedGoalTitleKey.WALK,
                icon = GoalIconType.DEFAULT,
                repeatCycle = RepeatCycle.MONTHLY,
                repeatCount = 2,
            ),
            RecommendedGoalPreset(
                id = READING_ID,
                titleKey = RecommendedGoalTitleKey.READING,
                icon = GoalIconType.BOOK,
                repeatCycle = RepeatCycle.MONTHLY,
                repeatCount = 4,
            ),
            RecommendedGoalPreset(
                id = CLEANING_ID,
                titleKey = RecommendedGoalTitleKey.CLEANING,
                icon = GoalIconType.CLEAN,
                repeatCycle = RepeatCycle.WEEKLY,
                repeatCount = 1,
            ),
            RecommendedGoalPreset(
                id = CALL_ON_WAY_HOME_ID,
                titleKey = RecommendedGoalTitleKey.CALL_ON_WAY_HOME,
                icon = GoalIconType.HEART,
                repeatCycle = RepeatCycle.DAILY,
                repeatCount = 1,
            ),
        )

    fun findById(id: String): RecommendedGoalPreset? = items.firstOrNull { it.id == id }
}
