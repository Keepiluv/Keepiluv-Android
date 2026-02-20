package com.twix.task_certification.editor.contract

import com.twix.ui.base.Intent

sealed interface TaskCertificationEditorIntent : Intent {
    data object Save : TaskCertificationEditorIntent

    data class CommentFocusChanged(
        val isFocused: Boolean,
    ) : TaskCertificationEditorIntent

    data class ModifyComment(
        val value: String,
    ) : TaskCertificationEditorIntent
}
