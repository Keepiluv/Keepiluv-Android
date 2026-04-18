package com.twix.goal_manage

import com.twix.ui.base.Intent
import java.time.LocalDate

sealed interface GoalManageIntent : Intent {
    data class SetSelectedDate(
        val date: LocalDate,
    ) : GoalManageIntent

    data class EndGoal(
        val id: Long,
    ) : GoalManageIntent

    data class DeleteGoal(
        val id: Long,
    ) : GoalManageIntent

    data object PreviousWeek : GoalManageIntent

    data object NextWeek : GoalManageIntent

    // UI 제어
    data class OpenMenu(
        val goalId: Long,
    ) : GoalManageIntent

    data object CloseMenu : GoalManageIntent

    data class ShowEndDialog(
        val goalId: Long,
    ) : GoalManageIntent

    data object DismissEndDialog : GoalManageIntent

    data class ShowDeleteDialog(
        val goalId: Long,
    ) : GoalManageIntent

    data object DismissDeleteDialog : GoalManageIntent

    data class EditGoal(
        val goalId: Long,
    ) : GoalManageIntent
}
