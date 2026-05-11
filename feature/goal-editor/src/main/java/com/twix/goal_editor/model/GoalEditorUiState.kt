package com.twix.goal_editor.model

import androidx.compose.runtime.Immutable
import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.RepeatCycle
import com.twix.result.AppError
import com.twix.ui.base.LoadableState
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
    val isSaving: Boolean = false,
    override val isLoading: Boolean = false,
    override val error: AppError? = null,
) : LoadableState {
    val isSaveEnabled: Boolean
        get() = goalTitle.isNotBlank()

    val isEndDateValid: Boolean
        get() = !endDateEnabled || !endDate.isBefore(startDate)

    val canSave: Boolean
        get() = isSaveEnabled && isEndDateValid && !isSaving

    override fun copyLoadableState(
        isLoading: Boolean,
        error: AppError?,
    ): LoadableState = copy(isLoading = isLoading, error = error)
}
