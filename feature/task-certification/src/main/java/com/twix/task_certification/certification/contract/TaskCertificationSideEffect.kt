package com.twix.task_certification.certification.contract

import com.twix.designsystem.components.toast.model.ToastType
import com.twix.ui.base.SideEffect

sealed interface TaskCertificationSideEffect : SideEffect {
    data class ShowToast(
        val message: Int,
        val type: ToastType,
    ) : TaskCertificationSideEffect

    data object NavigateToBack : TaskCertificationSideEffect

    data object NavigateToDetail : TaskCertificationSideEffect
}
