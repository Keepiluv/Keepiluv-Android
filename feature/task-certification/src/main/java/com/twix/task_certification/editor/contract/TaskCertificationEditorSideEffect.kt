package com.twix.task_certification.editor.contract

import com.twix.designsystem.components.toast.model.ToastType
import com.twix.ui.base.SideEffect

sealed interface TaskCertificationEditorSideEffect : SideEffect {
    data class ShowToast(
        val message: Int,
        val type: ToastType,
    ) : TaskCertificationEditorSideEffect
}
