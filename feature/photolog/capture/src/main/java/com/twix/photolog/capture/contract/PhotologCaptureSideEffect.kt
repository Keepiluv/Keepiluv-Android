package com.twix.photolog.capture.contract

import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.enums.BetweenUs
import com.twix.ui.base.SideEffect
import java.time.LocalDate

sealed interface PhotologCaptureSideEffect : SideEffect {
    data class ShowToast(
        val message: Int,
        val type: ToastType,
    ) : PhotologCaptureSideEffect

    data object NavigateToBack : PhotologCaptureSideEffect

    data class NavigateToDetail(
        val goalId: Long,
        val date: LocalDate,
        val betweenUs: BetweenUs,
    ) : PhotologCaptureSideEffect
}
