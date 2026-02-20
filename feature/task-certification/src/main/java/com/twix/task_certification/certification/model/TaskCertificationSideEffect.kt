package com.twix.task_certification.certification.model

import com.twix.designsystem.components.toast.model.ToastType
import com.twix.ui.base.SideEffect

sealed interface TaskCertificationSideEffect : SideEffect {
    data class ShowToast(
        val message: Int,
        val type: ToastType,
    ) : TaskCertificationSideEffect

    data object NavigateToDetail : TaskCertificationSideEffect
}
