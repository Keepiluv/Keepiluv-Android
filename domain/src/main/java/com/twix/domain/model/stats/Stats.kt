package com.twix.domain.model.stats

import java.time.LocalDate

data class Stats(
    val selectedDate: LocalDate,
    val statsGoals: List<StatsGoal>,
) {
    companion object {
        val EMPTY = Stats(selectedDate = LocalDate.now(), statsGoals = emptyList())
    }
}
