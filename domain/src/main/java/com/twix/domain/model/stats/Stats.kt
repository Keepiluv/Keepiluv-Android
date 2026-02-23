package com.twix.domain.model.stats

import java.time.LocalDate

data class Stats(
    val selectedDate: LocalDate,
    val statsGoals: List<StatsGoal>,
) {
    fun nextMonth(): Stats = copy(selectedDate = selectedDate.plusMonths(1))

    fun previousMonth(): Stats = copy(selectedDate = selectedDate.minusMonths(1))
}
