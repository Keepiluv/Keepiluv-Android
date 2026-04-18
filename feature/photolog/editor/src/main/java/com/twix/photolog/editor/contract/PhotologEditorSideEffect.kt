package com.twix.photolog.editor.contract

import com.twix.designsystem.components.toast.model.ToastType
import com.twix.ui.base.SideEffect

sealed interface PhotologEditorSideEffect : SideEffect {
    data class ShowToast(
        val message: Int,
        val type: ToastType,
    ) : PhotologEditorSideEffect

    data class ShowPokeToast(
        val message: String,
    ) : PhotologEditorSideEffect
}
