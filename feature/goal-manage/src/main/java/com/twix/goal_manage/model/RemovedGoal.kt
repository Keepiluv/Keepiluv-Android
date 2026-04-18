package com.twix.goal_manage.model

import com.twix.domain.model.goal.GoalSummary

data class RemovedGoal(
    val index: Int,
    val item: GoalSummary,
)
