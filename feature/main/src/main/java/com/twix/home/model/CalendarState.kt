package com.twix.home.model

import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
data class CalendarState(
    val visibleDate: LocalDate,
    val selectedDate: LocalDate,
    val monthYearText: String,
)
