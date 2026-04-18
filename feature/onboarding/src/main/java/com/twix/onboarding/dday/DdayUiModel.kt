package com.twix.onboarding.dday

import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
data class DdayUiModel(
    val anniversaryDate: LocalDate = LocalDate.now(),
    val isSelected: Boolean = false,
) {
    fun updateAnniversaryDate(value: LocalDate) = copy(anniversaryDate = value, isSelected = true)
}
