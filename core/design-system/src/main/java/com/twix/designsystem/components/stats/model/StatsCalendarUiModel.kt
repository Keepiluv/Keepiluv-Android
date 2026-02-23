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

val dummyStatsCalendarUiModel =
    StatsCalendarUiModel.create(
        currentDate = LocalDate.of(2025, 2, 1),
        completedDate =
            listOf(
                CompletedDate(LocalDate.of(2025, 2, 1), "https://picsum.photos/seed/1/200", "https://picsum.photos/seed/2/200"),
                CompletedDate(LocalDate.of(2025, 2, 3), "https://picsum.photos/seed/3/200", null),
                CompletedDate(LocalDate.of(2025, 2, 5), null, "https://picsum.photos/seed/4/200"),
                CompletedDate(LocalDate.of(2025, 2, 7), "https://picsum.photos/seed/5/200", "https://picsum.photos/seed/6/200"),
                CompletedDate(LocalDate.of(2025, 2, 8), "https://picsum.photos/seed/7/200", "https://picsum.photos/seed/8/200"),
                CompletedDate(LocalDate.of(2025, 2, 10), null, "https://picsum.photos/seed/9/200"),
                CompletedDate(LocalDate.of(2025, 2, 11), "https://picsum.photos/seed/10/200", null),
                CompletedDate(LocalDate.of(2025, 2, 13), "https://picsum.photos/seed/11/200", "https://picsum.photos/seed/12/200"),
                CompletedDate(LocalDate.of(2025, 2, 14), "https://picsum.photos/seed/13/200", "https://picsum.photos/seed/14/200"),
                CompletedDate(LocalDate.of(2025, 2, 15), null, null),
                CompletedDate(LocalDate.of(2025, 2, 17), "https://picsum.photos/seed/15/200", "https://picsum.photos/seed/16/200"),
                CompletedDate(LocalDate.of(2025, 2, 18), "https://picsum.photos/seed/17/200", null),
                CompletedDate(LocalDate.of(2025, 2, 19), null, "https://picsum.photos/seed/18/200"),
                CompletedDate(LocalDate.of(2025, 2, 20), "https://picsum.photos/seed/19/200", "https://picsum.photos/seed/20/200"),
                CompletedDate(LocalDate.of(2025, 2, 21), "https://picsum.photos/seed/21/200", "https://picsum.photos/seed/22/200"),
                CompletedDate(LocalDate.of(2025, 2, 22), null, "https://picsum.photos/seed/23/200"),
                CompletedDate(LocalDate.of(2025, 2, 24), "https://picsum.photos/seed/24/200", null),
                CompletedDate(LocalDate.of(2025, 2, 25), "https://picsum.photos/seed/25/200", "https://picsum.photos/seed/26/200"),
                CompletedDate(LocalDate.of(2025, 2, 27), null, null),
                CompletedDate(LocalDate.of(2025, 2, 28), "https://picsum.photos/seed/27/200", "https://picsum.photos/seed/28/200"),
            ),
    )
