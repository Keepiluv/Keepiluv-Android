package com.twix.photolog.detail.contract

import com.twix.designsystem.components.toast.model.ToastType
import com.twix.ui.base.SideEffect

sealed interface PhotologDetailSideEffect : SideEffect {
    data class ShowToast(
        val message: Int,
        val type: ToastType,
    ) : PhotologDetailSideEffect

    data class ShowPokeToast(
        val message: String,
    ) : PhotologDetailSideEffect

    data class ShowPokeCooldownToast(
        val remainingMs: Long,
    ) : PhotologDetailSideEffect
}
