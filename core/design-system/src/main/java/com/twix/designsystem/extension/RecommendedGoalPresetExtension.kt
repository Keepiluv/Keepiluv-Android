package com.twix.designsystem.extension

import androidx.annotation.StringRes
import com.twix.designsystem.R
import com.twix.domain.model.enums.RecommendedGoalTitleKey

@StringRes
fun RecommendedGoalTitleKey.toResId(): Int =
    when (this) {
        RecommendedGoalTitleKey.WORKOUT -> R.string.recommended_goal_workout
        RecommendedGoalTitleKey.VITAMIN -> R.string.recommended_goal_vitamin
        RecommendedGoalTitleKey.WALK -> R.string.recommended_goal_walk
        RecommendedGoalTitleKey.READING -> R.string.recommended_goal_reading
        RecommendedGoalTitleKey.CLEANING -> R.string.recommended_goal_cleaning
        RecommendedGoalTitleKey.CALL_ON_WAY_HOME -> R.string.recommended_goal_call_on_way_home
    }
