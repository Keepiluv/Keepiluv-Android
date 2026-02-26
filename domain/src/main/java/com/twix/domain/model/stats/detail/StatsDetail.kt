package com.twix.domain.model.stats.detail

import com.twix.domain.model.enums.GoalIconType
import java.time.LocalDate

data class StatsDetail(
    val goalId: Long,
    val goalName: String,
    val goalIcon: GoalIconType,
    val isCompleted: Boolean,
    val currentDate: LocalDate,
    val completedDate: List<CompletedDate>,
) {
    companion object {
        val EMPTY =
            StatsDetail(
                goalId = -1,
                goalName = "",
                goalIcon = GoalIconType.DEFAULT,
                isCompleted = false,
                currentDate = LocalDate.now(),
                completedDate = emptyList(),
            )
    }
}
