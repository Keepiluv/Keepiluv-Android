package com.twix.designsystem.components.comment.model

import androidx.compose.runtime.Immutable

@Immutable
data class CommentUiModel(
    val value: String = "",
    val isFocused: Boolean = false,
) {
    val hasMaxCommentLength: Boolean
        get() = value.length == COMMENT_COUNT

    val canUpload: Boolean
        get() =
            value.isEmpty() ||
                value.isNotEmpty() &&
                hasMaxCommentLength

    companion object {
        const val COMMENT_COUNT = 5
    }
}
