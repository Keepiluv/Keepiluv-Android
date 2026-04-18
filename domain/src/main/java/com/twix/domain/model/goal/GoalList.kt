package com.twix.domain.model.goal

data class GoalList(
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val goals: List<Goal> = emptyList(),
)
