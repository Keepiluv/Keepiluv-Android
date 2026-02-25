package com.twix.domain.model.stats.detail

import com.twix.domain.model.enums.GoalIconType
import java.time.LocalDate

data class StatsDetail(
    val goalId: Long,
    val goalName: String,
    val goalIcon: GoalIconType,
    val isCompleted: Boolean,
    val yearMonth: LocalDate,
    val completedDate: List<CompletedDate>,
) {
    companion object {
        val EMPTY =
            StatsDetail(
                goalId = -1,
                goalName = "",
                goalIcon = GoalIconType.DEFAULT,
                isCompleted = false,
                yearMonth = LocalDate.now(),
                completedDate = emptyList(),
            )
    }
}
