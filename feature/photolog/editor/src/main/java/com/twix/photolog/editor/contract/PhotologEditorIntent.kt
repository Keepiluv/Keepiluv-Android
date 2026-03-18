package com.twix.photolog.editor.contract

import com.twix.ui.base.Intent

sealed interface PhotologEditorIntent : Intent {
    data object Save : PhotologEditorIntent

    data class CommentFocusChanged(
        val isFocused: Boolean,
    ) : PhotologEditorIntent

    data class ModifyComment(
        val value: String,
    ) : PhotologEditorIntent
}
