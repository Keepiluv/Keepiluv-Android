package com.twix.task_certification.certification.contract

import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.enums.BetweenUs
import com.twix.ui.base.SideEffect
import java.time.LocalDate

sealed interface TaskCertificationSideEffect : SideEffect {
    data class ShowToast(
        val message: Int,
        val type: ToastType,
    ) : TaskCertificationSideEffect

    data object NavigateToBack : TaskCertificationSideEffect

    data class NavigateToDetail(
        val goalId: Long,
        val date: LocalDate,
        val betweenUs: BetweenUs,
    ) : TaskCertificationSideEffect
}
