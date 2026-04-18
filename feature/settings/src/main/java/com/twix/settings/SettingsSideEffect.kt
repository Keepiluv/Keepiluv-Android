package com.twix.settings

import androidx.annotation.StringRes
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.ui.base.SideEffect

sealed interface SettingsSideEffect : SideEffect {
    data class ShowToast(
        @param:StringRes val resId: Int,
        val type: ToastType,
    ) : SettingsSideEffect

    data object NavigateToLogin : SettingsSideEffect
}
