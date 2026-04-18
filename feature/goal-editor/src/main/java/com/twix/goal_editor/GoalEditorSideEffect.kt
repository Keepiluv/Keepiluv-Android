package com.twix.goal_editor

import androidx.annotation.StringRes
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.ui.base.SideEffect

interface GoalEditorSideEffect : SideEffect {
    data class ShowToast(
        @param:StringRes val resId: Int,
        val type: ToastType,
    ) : GoalEditorSideEffect

    object NavigateToHome : GoalEditorSideEffect
}
