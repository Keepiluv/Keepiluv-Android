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

    fun updateCommentFocus(isFocus: Boolean) = copy(comment = comment.updateFocus(isFocus))

    fun updateComment(value: String) = copy(comment = comment.updateComment(value))

    val imageName: String
        get() = imageUrl.split(IMAGE_NAME_SEPARATOR).last()

    fun updateInitialState(serializer: EditorNavArgs) =
        copy(
            goalId = serializer.goalId,
            nickname = serializer.nickname,
            goalName = serializer.goalName,
            photologId = serializer.photologId,
            imageUrl = serializer.imageUrl,
            comment = CommentUiModel(serializer.comment.orEmpty()),
            originComment = serializer.comment.orEmpty(),
        )

    companion object {
        private const val IMAGE_NAME_SEPARATOR = "/"
    }
}
