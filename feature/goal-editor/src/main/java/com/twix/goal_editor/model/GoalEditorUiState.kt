package com.twix.goal_editor.model

import androidx.compose.runtime.Immutable
import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.RepeatCycle
import com.twix.ui.base.State
import java.time.LocalDate

@Immutable
data class GoalEditorUiState(
    val selectedIcon: GoalIconType = GoalIconType.DEFAULT,
    val goalTitle: String = "",
    val selectedRepeatCycle: RepeatCycle = RepeatCycle.DAILY,
    val repeatCount: Int = 1,
    val startDate: LocalDate = LocalDate.now(),
    val endDateEnabled: Boolean = false,
    val endDate: LocalDate = LocalDate.now(),
) : State {
    val isEnabled: Boolean
        get() = goalTitle.isNotBlank()
}
