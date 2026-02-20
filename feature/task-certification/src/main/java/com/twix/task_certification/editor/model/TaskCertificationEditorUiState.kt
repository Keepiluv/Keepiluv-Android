package com.twix.task_certification.editor.model

import androidx.compose.runtime.Immutable
import com.twix.designsystem.components.comment.model.CommentUiModel
import com.twix.navigation.args.EditorNavArgs
import com.twix.ui.base.State

@Immutable
data class TaskCertificationEditorUiState(
    val goalId: Long = -1,
    val photologId: Long = -1,
    val nickname: String = "",
    val goalName: String = "",
    val imageUrl: String = "",
    val comment: CommentUiModel = CommentUiModel(),
    val originComment: String = "",
) : State {
    val isCommentNotChanged: Boolean
        get() = comment.value == originComment

    val imageName: String
        get() = imageUrl.split(IMAGE_NAME_SEPARATOR).last()

    companion object {
        private const val IMAGE_NAME_SEPARATOR = "/"
    }
}

internal fun EditorNavArgs.toUiState() =
    TaskCertificationEditorUiState(
        goalId = goalId,
        nickname = nickname,
        goalName = goalName,
        photologId = photologId,
        imageUrl = imageUrl,
        comment = CommentUiModel(comment.orEmpty()),
        originComment = comment.orEmpty(),
    )
