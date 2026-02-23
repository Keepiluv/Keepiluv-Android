package com.yapp.detail.contract

import com.twix.designsystem.components.toast.model.ToastType
import com.twix.ui.base.SideEffect

sealed interface StatsDetailSideEffect : SideEffect {
    data class ShowToast(
        val message: Int,
        val type: ToastType,
    ) : StatsDetailSideEffect

    data object NavigateToBack : StatsDetailSideEffect
}
