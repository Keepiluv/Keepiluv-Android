package com.twix.designsystem.components.stats.model

import androidx.compose.runtime.Immutable
import com.twix.domain.model.stats.detail.CompletedDate
import java.time.LocalDate

@Immutable
data class StatsCalendarUiModel(
    val currentDate: LocalDate = LocalDate.now(),
    val completedDateMap: Map<LocalDate, CompletedDate> = emptyMap(),
    val weeks: List<List<LocalDate?>> = emptyList(),
) {
    companion object {
        private const val WEEK_LENGTH = 7

        fun create(
            currentDate: LocalDate,
            completedDate: List<CompletedDate>,
        ): StatsCalendarUiModel {
            val firstDayOfMonth = currentDate.withDayOfMonth(1)
            val lastDay = currentDate.lengthOfMonth()
            val emptyCellsBefore = firstDayOfMonth.dayOfWeek.value % WEEK_LENGTH

            val calendarItems = mutableListOf<LocalDate?>()
            repeat(emptyCellsBefore) { calendarItems.add(null) }
            for (i in 1..lastDay) {
                calendarItems.add(currentDate.withDayOfMonth(i))
            }

            return StatsCalendarUiModel(
                currentDate = currentDate,
                completedDateMap = completedDate.associateBy { it.date },
                weeks = calendarItems.chunked(WEEK_LENGTH),
            )
        }
    }
}
