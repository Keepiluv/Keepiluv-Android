package com.twix.stats.detail.contract

import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.enums.BetweenUs
import com.twix.ui.base.SideEffect
import java.time.LocalDate

sealed interface StatsDetailSideEffect : SideEffect {
    data class ShowToast(
        val message: Int,
        val type: ToastType,
    ) : StatsDetailSideEffect

    data object NavigateToBack : StatsDetailSideEffect

    data class NavigateToTaskCertificationDetail(
        val goalId: Long,
        val date: LocalDate,
        val betweenUs: BetweenUs,
    ) : StatsDetailSideEffect
}
