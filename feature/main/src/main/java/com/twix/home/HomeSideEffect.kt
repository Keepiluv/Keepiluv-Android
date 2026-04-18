package com.twix.home

import androidx.annotation.StringRes
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.ui.base.SideEffect

sealed interface HomeSideEffect : SideEffect {
    data object ShowMonthPickerBottomSheet : HomeSideEffect

    data class ShowToast(
        @param:StringRes val resId: Int,
        val type: ToastType,
    ) : HomeSideEffect

    data class ShowPokeToast(
        val message: String,
    ) : HomeSideEffect

    data object ShowPermissionLauncher : HomeSideEffect
}
