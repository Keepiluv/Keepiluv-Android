package com.twix.goal_manage

import androidx.annotation.StringRes
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.ui.base.SideEffect

sealed interface GoalManageSideEffect : SideEffect {
    data class ShowToast(
        @param:StringRes val resId: Int,
        val type: ToastType,
    ) : GoalManageSideEffect

    data class NavigateToGoalEditor(
        val goalId: Long,
    ) : GoalManageSideEffect
}
