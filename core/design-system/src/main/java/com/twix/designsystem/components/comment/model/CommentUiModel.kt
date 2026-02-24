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

    fun updateComment(newComment: String): CommentUiModel = copy(value = newComment)

    fun updateFocus(isFocused: Boolean) = copy(isFocused = isFocused)

    companion object {
        const val COMMENT_COUNT = 5
    }
}
