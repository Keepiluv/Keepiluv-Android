package com.twix.goal_editor

import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.RepeatCycle
import com.twix.ui.base.Intent
import java.time.LocalDate

sealed interface GoalEditorIntent : Intent {
    data class SetIcon(
        val icon: GoalIconType,
    ) : GoalEditorIntent

    data class SetTitle(
        val title: String,
    ) : GoalEditorIntent

    data class SetRepeatType(
        val repeatCycle: RepeatCycle,
    ) : GoalEditorIntent

    data class SetRepeatCount(
        val repeatCount: Int,
    ) : GoalEditorIntent

    data class SetStartDate(
        val startDate: LocalDate,
    ) : GoalEditorIntent

    data class SetEndDate(
        val endDate: LocalDate,
    ) : GoalEditorIntent

    data class SetEndDateEnabled(
        val enabled: Boolean,
    ) : GoalEditorIntent

    data class Save(
        val id: Long,
    ) : GoalEditorIntent

    data class InitGoal(
        val id: Long,
    ) : GoalEditorIntent
}
